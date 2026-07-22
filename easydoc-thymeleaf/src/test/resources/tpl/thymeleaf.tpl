<html>
<head>
    <title>Thymeleaf!!!!!</title>
    <meta http-equiv="Content-Type" content="text/html;" th:charset="${target}"/>
    <style type="text/css">
        body { font-size: 10pt; color: #333333; }
        thead { font-weight: bold; background-color: #C8FBAF; }
        td { font-size: 10pt; text-align: center; }
        .odd { background-color: #F3DEFB; }
        .even { background-color: #EFFFF8; }
    </style>
</head>
<body>
    <h1>Template Engine Benchmark - Thymeleaf!!!!!</h1>
    <table>
        <thead>
            <tr>
                <th>序号</th>
                <th>编码</th>
                <th>名称</th>
                <th>日期</th>
                <th>值</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="model : ${models}" th:class="${modelStat.odd}? 'odd' : 'even'"  >
                <td th:text="${modelStat.index}"></td>
                <td th:text="${model.code}"></td>
                <td th:text="${model.name}"></td>
                <td th:text="${model.date}"></td>
                <td th:text="${model.value}"></td>
                <td th:if="${model.value} > 105.5" style="color: red" th:text="${model.value}+'%'"></td>
                <td th:unless="${model.value} > 105.5" style="color: blue" th:text="${model.value}+'%'"></td>
            </tr>
        </tbody>
    </table>
</body>
</html>