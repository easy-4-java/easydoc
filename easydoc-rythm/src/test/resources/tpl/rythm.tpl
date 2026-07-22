@import java.text.*
@import java.util.*
@args String target
@args boolean format
@args java.util.List<Map<String, Object>> models
<html>
<head>
    <title>Rythm!!!!!</title>
    <meta http-equiv="Content-Type" content="text/html; charset=@raw(){@target}"/>
    <style type="text/css">
        body { font-size: 10pt; color: #333333; }
        thead { font-weight: bold; background-color: #C8FBAF; }
        td { font-size: 10pt; text-align: center; }
        .odd { background-color: #F3DEFB; }
        .even { background-color: #EFFFF8; }
    </style>
</head>
<body>
    <h1>Kiang TEB - Rythm!!!!!</h1>
    <table>
        <thead>
            <tr>
                <th width="40px">序号</th>
                <th width="40px">编码</th>
                <th width="120px">名称</th>
                <th width="120px">日期</th>
                <th width="40px">布尔</th>
                <th width="80px">值</th>
            </tr>
        </thead>
        <tbody>
        @raw() {
            @for(model: models) {
                @{
                    String clazz = model_index % 2 ==0 ? "odd" : "even";
                }
            <tr class="@clazz">
                <td>@model_index</td>
                <td>@model.getCode()</td>
                <td>@model.getName()</td>
                @if(format) {
                <td>@DateUtil.format(model.getDate(), "yyyy-MM-dd HH:mm:ss")</td>
                } else {
                <td>@model.getDate()</td>
                }
                <td>@model.isBool()</td>
                @if(model.getValue() > 105.5) {
                <td style="color: red;">@model.getValue()</td>
                } else {
                <td style="color: blue;">@model.getValue()</td>
                }
            </tr>
            }
        }
        </tbody>
    </table>
</body>
</html>