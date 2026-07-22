<html>
<head>
    <title>Velocity!</title>
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
    <h1>Kiang TEB - Velocity!</h1>
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
            #foreach($model in $models)
            #if($velocityCount % 2 == 0) #set($klass = "even") #else #set($klass = "odd") #end
            <tr class="${klass}">
                <td>${velocityCount}</td>
                <td>${model.code}</td>
                <td>${model.name}</td>
                #if($format)
                <td>$dateTool.format("yyyy-MM-dd HH:mm:ss", $model.date)</td>
                #else
                <td>${model.date}</td>
                #end
                <td>${model.bool}</td>
                #if($model.value > 105.5)
                <td style="color: red;">${model.value}</td>
                #else
                <td style="color: blue;">${model.value}</td>
                #end
            </tr>
            #end
        </tbody>
    </table>
</body>
</html>