package io.github.easy4j.doc.rythm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rythmengine.utils.JSONWrapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * rythm-engine 传递依赖 fastjson 的兼容性冒烟测试。
 *
 * <p>背景：rythm 在 {@code utils/JSONWrapper}、{@code template/TemplateBase}、
 * {@code utils/S} 中硬编码 fastjson <b>v1</b> API（com.alibaba.fastjson.*），且无 JSON 抽象层/SPI。
 * 1.2.x 线已终止维护并存在无修复的 CVE-2026-16723（影响 1.2.68–1.2.83），
 * 因此本工程将 effective 版本锁定到 <b>2.0.x v1 兼容构建</b>——同一 GAV，
 * 包名与 API 面保持 v1，内部实现切换为 fastjson2 内核。</p>
 *
 * <p>本测试锁定 rythm 实际调用的那部分 v1 API 面，防止未来误把依赖降回 1.2.x
 * 或升级到破坏 v1 兼容面的版本时无人察觉：</p>
 * <ul>
 *   <li>{@code JSON.parse(String)} —— JSONWrapper 构造路径</li>
 *   <li>{@code JSON.parseArray(String, Class)} / {@code JSON.parseObject(String, Class)}
 *       —— TemplateBase.__setRenderArg(JSONWrapper) 的带类型参数绑定路径</li>
 *   <li>JSONArray 的 {@code size()/get(int)/toString()} —— TemplateBase.setJSONArray/setJSONObject</li>
 *   <li>{@code JSONWrapper#isArray()/getArray()/getObject()} —— rythm 自身类在兼容构建下的行为</li>
 * </ul>
 */
class RythmFastjsonCompatTest {

	@Test
	@DisplayName("JSON.parse — JSONWrapper 构造 + 对象访问路径")
	void jsonParseViaJsonWrapper() {
		JSONWrapper wrapper = new JSONWrapper("{\"name\":\"张三\",\"age\":30}");
		assertFalse(wrapper.isArray());
		Map<String, Object> obj = wrapper.getObject();
		assertEquals("张三", obj.get("name"));
		assertEquals(30, obj.get("age"));
	}

	@Test
	@DisplayName("JSONWrapper 数组路径 — isArray/getArray")
	void jsonWrapperArrayPath() {
		JSONWrapper wrapper = new JSONWrapper("[{\"id\":1},{\"id\":2}]");
		assertTrue(wrapper.isArray());
		List<Object> list = wrapper.getArray();
		assertEquals(2, list.size());
	}

	@Test
	@DisplayName("JSON.parseObject/parseArray 带类型绑定 — TemplateBase 渲染参数路径")
	void typedBindingSurface() {
		JSONObject obj = JSON.parseObject("{\"title\":\"t\",\"n\":2}", JSONObject.class);
		assertEquals("t", obj.getString("title"));
		assertEquals(2, obj.getIntValue("n"));

		List<String> arr = JSON.parseArray("[\"a\",\"b\"]", String.class);
		assertEquals(2, arr.size());
		assertEquals("b", arr.get(1));
	}

	@Test
	@DisplayName("对象→数组元素逐个 parseObject — setJSONObject 循环模式（含 size/get/toString）")
	void elementWiseParse() {
		String arrayStr = "[{\"k\":\"v1\"},{\"k\":\"v2\"}]";
		JSONArray raw = (JSONArray) JSON.parse(arrayStr);
		assertEquals(2, raw.size());
		for (int i = 0; i < raw.size(); i++) {
			Object el = raw.get(i);
			JSONObject parsed = JSON.parseObject(el.toString(), JSONObject.class);
			assertEquals("v" + (i + 1), parsed.getString("k"));
		}
	}
}
