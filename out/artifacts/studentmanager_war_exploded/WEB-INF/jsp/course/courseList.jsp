<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>课程列表</title>
    <link href='<c:url context="${pageContext.request.contextPath}" value="/easyui/themes/default/easyui.css"/>'
          rel="stylesheet" type="text/css"/>
    <link href='<c:url context="${pageContext.request.contextPath}" value="/easyui/themes/icon.css"/>' rel="stylesheet"
          type="text/css"/>
    <link href='<c:url context="${pageContext.request.contextPath}" value="/easyui/css/demo.css"/>' rel="stylesheet"
          type="text/css"/>
    <script type="text/javascript"
            src='<c:url value="/easyui/jquery.min.js" context="${pageContext.request.contextPath}"/>'></script>
    <script type="text/javascript"
            src='<c:url value="/easyui/jquery.easyui.min.js" context="${pageContext.request.contextPath}"/>'></script>
    <script type="text/javascript"
            src='<c:url value="/easyui/js/validateExtends.js" context="${pageContext.request.contextPath}"/>'></script>
    <script type="text/javascript"
            src='<c:url value="/easyui/js/easyui-lang-zh_CN.js" context="${pageContext.request.contextPath}"/>'></script>

    <script type="text/javascript">
        $(function () {
            //datagrid初始化
            $('#dataList').datagrid({
                title: '课程列表',
                iconCls: 'icon-more',//图标
                border: true,
                collapsible: false,//是否可折叠的
                fit: true,//自动大小
                method: "get",//请求表格数据的时候，请求方法
                url: "/s/course?action=data",
                idField: 'cid',//表格每一行的唯一标识符
                singleSelect: true,//是否单选
                pagination: false,//分页控件
                rownumbers: true,//行号
                sortName: 'cid',
                sortOrder: 'asc',
                remoteSort: false,
                columns: [[
                    {field: 'chk', checkbox: true, width: 50},
                    {field: 'cid', title: '课程编号', width: 80, sortable: true},
                    {field: 'courseName', title: '课程名称', width: 200},
                ]],
                toolbar: "#toolbar"
            });

            //设置工具类按钮
            $("#add").click(function () {
                //弹一个对话框
                $("#addDialog").dialog("open");
            });
            //删除按钮点击事件
            $("#delete").click(function () {
                //返回课程表第一个被选中的行或如果没有选中的行则返回null。
                //如果当前有选中的行，那么返回的数据则是这一行所对应的 JSON，{cid:27,courseName:'bb'}
                var selectRow = $("#dataList").datagrid("getSelected");
                if (selectRow == null) {
                    $.messager.alert("消息提醒", "请选择数据进行删除!", "warning");
                } else {
                    //先获取选中行的课程id
                    var courseid = selectRow.cid;
                    $.messager.confirm("消息提醒", "将删除与【" + selectRow.courseName + "】课程相关的所有数据，确认继续？", function (r) {
                        if (r) {
                            //如果用户点击了确认按钮，就会进入到 if 中
                            $.ajax({
                                type: "delete",
                                url: "/s/course?cid=" + courseid,
                                success: function (msg) {
                                    if (msg.status == 200) {
                                        $.messager.alert("消息提醒", msg.msg, "info");
                                        //刷新表格
                                        $("#dataList").datagrid("reload");
                                        //取消当前表格中所有选中的行
                                        $("#dataList").datagrid("uncheckAll");
                                    } else {
                                        $.messager.alert("消息提醒", msg.msg, "warning");
                                        return;
                                    }
                                }
                            });
                        }
                    });
                }
            });

            //设置添加窗口
            //添加课程的对话框
            $("#addDialog").dialog({
                title: "添加课程",
                width: 450,
                height: 250,
                iconCls: "icon-add",
                modal: true,//是否是一个模态对话框
                collapsible: false,//是否可折叠
                minimizable: false,//是否可以最小化
                maximizable: false,//是否可以最大化
                draggable: true,//是否可以拖拽
                closed: true,//是否可以关闭
                buttons: [
                    {
                        text: '添加',
                        plain: true,
                        iconCls: 'icon-book-add',
                        handler: function () {
                            var validate = $("#addForm").form("validate");
                            if (!validate) {
                                $.messager.alert("消息提醒", "请检查你输入的数据!", "warning");
                                return;
                            } else {
                                let data = $("#addForm").serialize();// courseName=xxx
                                $.ajax({
                                    type: "post",
                                    url: "/s/course",
                                    data: data,
                                    success: function (msg) {
                                        if (msg.status == 200) {
                                            $.messager.alert("消息提醒", "添加成功!", "info");
                                            //关闭窗口
                                            $("#addDialog").dialog("close");
                                            //清空原表格数据
                                            $("#add_name").textbox('setValue', "");
                                            //刷新表格
                                            $('#dataList').datagrid("reload");
                                        } else {
                                            $.messager.alert("消息提醒", msg.msg, "warning");
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '重置',
                        plain: true,
                        iconCls: 'icon-book-reset',
                        handler: function () {
                            $("#add_name").textbox('setValue', "");
                        }
                    },
                ]
            });

        });
    </script>
</head>
<body>
<!-- 数据列表 ，这个就是课程列表的表格-->
<table id="dataList" cellspacing="0" cellpadding="0">

</table>
<!-- 工具栏 -->
<div id="toolbar">
    <div style="float: left;"><a id="add" href="javascript:;" class="easyui-linkbutton"
                                 data-options="iconCls:'icon-add',plain:true">添加</a></div>
    <div style="float: left;" class="datagrid-btn-separator"></div>
    <div><a id="delete" href="javascript:;" class="easyui-linkbutton"
            data-options="iconCls:'icon-some-delete',plain:true">删除</a></div>
</div>

<!-- 添加数据窗口 -->
<div id="addDialog" style="padding: 10px">
    <form id="addForm" method="post">
        <table cellpadding="8">
            <tr>
                <td>课程名称:</td>
                <td><input id="add_name" style="width: 200px; height: 30px;" class="easyui-textbox" type="text"
                           name="courseName"
                           data-options="required:true, validType:'repeat_course', missingMessage:'不能为空'"/></td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>