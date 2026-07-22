<html>
<head>
    <title>Beetl!!!!!</title>
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
    <h1>Kiang TEB - Beetl!!!!!</h1>
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
            <% for(model in models) { %>
            <tr class="${modelLP.odd ? "even" : "odd"}">
                <td>${modelLP.index}</td>
                <td>${model.code}</td>
                <td>${model.name}</td>
                <% if(format) { %>
                <td>${model.date, dateFormat="yyyy-MM-dd HH:mm:ss"}</td>
                <% } else { %>
                <td>${model.date}</td>
                <% } %>
                <td>${model.bool}</td>
                <% if(model.value > 105.5) { %>
                <td style="color: red;">${model.value}</td>
                <% } else { %>
                <td style="color: blue;">${model.value}</td>
                <% } %>
            </tr>
            <% } %>
        </tbody>
    </table>
</body>
</html>