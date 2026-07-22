<html>
<head>
    <title>FreeMarker</title>
    <meta http-equiv="Content-Type" content="text/html; charset=${target}"/>
    <style type="text/css">
        body { font-size: 10pt; color: #333333; }
        thead { font-weight: bold; background-color: #C8FBAF; }
        td { font-size: 10pt; text-align: center; }
        .odd { background-color: #F3DEFB; }
        .even { background-color: #EFFFF8; }
    </style>
</head>
<body>
    <h1>Kiang TEB - FreeMarker</h1>
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
            <#list models as model>
            <tr class="${["odd", "even"][model_index % 2]}">
                <td>${model_index}</td>
                <td>${model.code}</td>
                <td>${model.name}</td>
                <#if (format)>
                <td>${model.date?string("yyyy-MM-dd HH:mm:ss")}</td>
                <#else>
                <td>${String.valueOf(model.date)}</td>
                </#if>
                <td>${model.bool?string("true", "false")}</td>
                <#if (model.value > 105.5)>
                <td style="color: red;">${model.value}</td>
                <#else>
                <td style="color: blue;">${model.value}</td>
                </#if>
            </tr>
            </#list>
        </tbody>
    </table>
</body>
</html>