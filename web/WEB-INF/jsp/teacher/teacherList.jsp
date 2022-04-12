<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>教师列表</title>
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
        $(function() {
            var table;

            //datagrid初始化
            $('#dataList').datagrid({
                title:'教师列表',
                iconCls:'icon-more',//图标
                border: true,
                collapsible:false,//是否可折叠的
                fit: true,//自动大小
                method: "get",
                url:"/s/teacher?action=data",
                idField:'tid',
                singleSelect:false,//是否单选
                pagination:true,//分页控件
                rownumbers:true,//行号
                sortName:'tid',
                pageSize: 2,
                pageList: [2, 4, 6, 8, 10],
                sortOrder:'asc',
                remoteSort: false,
                columns: [[
                    {field:'chk',checkbox: true,width:50},
                    {field:'tid',title:'教师编号',width:80, sortable: true},
                    {field:'number',title:'工号',width:120, sortable: true},
                    {field:'name',title:'姓名',width:150},
                    {field:'gender',title:'性别',width:60},
                    {field:'phone',title:'电话',width:120},
                    {field:'qq',title:'QQ',width:150},
                    {field:'courses',title:'课程',width:500,
                        formatter: function(value,row,index){
                            if (row.courses){
                                //获取该教师的所带课程对应的数组
                                var courseList = row.courses;
                                var course = "";
                                for(var i = 0;i < courseList.length;i++){
                                    var gradeName = courseList[i].grade.gradeName;
                                    var clazzName = courseList[i].clazz.clazzName;
                                    var courseName = courseList[i].course.courseName;
                                    course += "[" + gradeName + " " + clazzName + " " + courseName + "] &nbsp;&nbsp;&nbsp;";
                                    //[xx xx xx][xx xx xx]
                                }
                                return course;
                            } else {
                                return value;
                            }
                        }
                    }
                ]],
                toolbar: "#toolbar"
            });
            //设置分页控件
            var p = $('#dataList').datagrid('getPager');
            $(p).pagination({
                // pageSize: 10,//每页显示的记录条数，默认为10
                // pageList: [10,20,30,50,100],//可以设置每页记录条数的列表
                beforePageText: '第',//页数文本框前显示的汉字
                afterPageText: '页    共 {pages} 页',
                displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
            });
            //设置工具类按钮
            $("#add").click(function(){
                table = $("#addTable");
                $("#addDialog").dialog("open");
            });
            //修改
            $("#edit").click(function(){
                table = $("#editTable");
                //返回所有被选中的行，当没有记录被选中的时候将返回一个空数组。[{},{},{}]
                var selectRows = $("#dataList").datagrid("getSelections");
                if(selectRows.length != 1){
                    $.messager.alert("消息提醒", "请选择一条数据进行操作!", "warning");
                } else{
                    //如果只选中了一行，就打开修改教师信息的对话框
                    $("#editDialog").dialog("open");
                }
            });
            //删除
            $("#delete").click(function(){
                var selectRows = $("#dataList").datagrid("getSelections");
                var selectLength = selectRows.length;
                if(selectLength == 0){
                    $.messager.alert("消息提醒", "请选择数据进行删除!", "warning");
                } else{
                    var ids = [];
                    $(selectRows).each(function(i, row){
                        ids[i] = row.id;
                    });
                    var numbers = [];
                    $(selectRows).each(function(i, row){
                        numbers[i] = row.number;
                    });
                    $.messager.confirm("消息提醒", "将删除与教师相关的所有数据，确认继续？", function(r){
                        if(r){
                            $.ajax({
                                type: "post",
                                url: "TeacherServlet?method=DeleteTeacher",
                                data: {ids: ids,numbers:numbers},
                                success: function(msg){
                                    if(msg == "success"){
                                        $.messager.alert("消息提醒","删除成功!","info");
                                        //刷新表格
                                        $("#dataList").datagrid("reload");
                                        $("#dataList").datagrid("uncheckAll");
                                    } else{
                                        $.messager.alert("消息提醒","删除失败!","warning");
                                        return;
                                    }
                                }
                            });
                        }
                    });
                }
            });

            //设置添加窗口
            $("#addDialog").dialog({
                title: "添加教师",
                width: 850,
                height: 550,
                iconCls: "icon-add",
                modal: true,
                collapsible: false,
                minimizable: false,
                maximizable: false,
                draggable: true,
                closed: true,
                buttons: [
                    {
                        text:'设置课程',
                        plain: true,
                        iconCls:'icon-book-add',
                        handler:function(){
                            $("#chooseCourseDialog").dialog("open");
                        }
                    },
                    {
                        text:'添加',
                        plain: true,
                        iconCls:'icon-user_add',
                        handler:function(){
                            var validate = $("#addForm").form("validate");
                            if(!validate){
                                $.messager.alert("消息提醒","请检查你输入的数据!","warning");
                                return;
                            } else{
                                var chooseCourse = [];
                                $(table).find(".chooseTr").each(function(){
                                    var gradeid = $(this).find("input[textboxname='gradeid']").attr("gradeId");
                                    var clazzid = $(this).find("input[textboxname='clazzid']").attr("clazzId");
                                    var courseid = $(this).find("input[textboxname='courseid']").attr("courseId");
                                    var course = gradeid+"_"+clazzid+"_"+courseid;
                                    chooseCourse.push(course);
                                });
                                var number = $("#add_number").textbox("getText");
                                var name = $("#add_name").textbox("getText");
                                var sex = $("#add_sex").textbox("getText");
                                var phone = $("#add_phone").textbox("getText");
                                var qq = $("#add_qq").textbox("getText");
                                var data = {number:number, name:name,sex:sex,phone:phone,qq:qq,course:chooseCourse};

                                $.ajax({
                                    type: "post",
                                    url: "/s/teacher",
                                    data: data,
                                    success: function(msg){
                                        if(msg.status == 200){
                                            $.messager.alert("消息提醒","添加成功!","info");
                                            //关闭窗口
                                            $("#addDialog").dialog("close");
                                            //清空原表格数据
                                            $("#add_number").textbox('setValue', "");
                                            $("#add_name").textbox('setValue', "");
                                            $("#add_sex").textbox('setValue', "男");
                                            $("#add_phone").textbox('setValue', "");
                                            $("#add_qq").textbox('setValue', "");
                                            $(table).find(".chooseTr").remove();

                                            //重新刷新页面数据
                                            $('#dataList').datagrid("reload");

                                        } else{
                                            $.messager.alert("消息提醒",msg.msg,"warning");
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    },
                    {
                        text:'重置',
                        plain: true,
                        iconCls:'icon-reload',
                        handler:function(){
                            $("#add_number").textbox('setValue', "");
                            $("#add_name").textbox('setValue', "");
                            $("#add_phone").textbox('setValue', "");
                            $("#add_qq").textbox('setValue', "");

                            $(table).find(".chooseTr").remove();

                        }
                    },
                ],
                onClose: function(){
                    $("#add_number").textbox('setValue', "");
                    $("#add_name").textbox('setValue', "");
                    $("#add_phone").textbox('setValue', "");
                    $("#add_qq").textbox('setValue', "");

                    $(table).find(".chooseTr").remove();
                }
            });

            //设置课程窗口
            $("#chooseCourseDialog").dialog({
                title: "设置课程",
                width: 400,
                height: 300,
                iconCls: "icon-add",
                modal: true,
                collapsible: false,
                minimizable: false,
                maximizable: false,
                draggable: true,
                closed: true,
                buttons: [
                    {
                        text:'添加',
                        plain: true,
                        iconCls:'icon-book-add',
                        handler:function(){
                            //添加之前先判断是否已选择该课程
                            var chooseCourse = [];
                            $(table).find(".chooseTr").each(function(){
                                var gradeid = $(this).find("input[textboxname='gradeid']").attr("gradeId");
                                var clazzid = $(this).find("input[textboxname='clazzid']").attr("clazzId");
                                var courseid = $(this).find("input[textboxname='courseid']").attr("courseId");
                                var course = gradeid+"_"+clazzid+"_"+courseid;
                                chooseCourse.push(course);
                            });
                            //获取新选择的课程
                            var gradeId = $("#add_gradeList").combobox("getValue");
                            var clazzId = $("#add_clazzList").combobox("getValue");
                            var courseId = $("#add_courseList").combobox("getValue");
                            var newChoose = gradeId+"_"+clazzId+"_"+courseId;
                            for(var i = 0;i < chooseCourse.length;i++){
                                if(newChoose == chooseCourse[i]){
                                    $.messager.alert("消息提醒","已选择该门课程!","info");
                                    return;
                                }
                            }

                            //添加到表格显示
                            var tr = $("<tr class='chooseTr'><td>课程:</td></tr>");

                            var gradeName = $("#add_gradeList").combobox("getText");
                            var gradeTd = $("<td></td>");
                            var gradeInput = $("<input style='width: 200px; height: 30px;' data-options='readonly: true' class='easyui-textbox' name='gradeid' />").val(gradeName).attr("gradeId", gradeId);
                            $(gradeInput).appendTo(gradeTd);
                            $(gradeTd).appendTo(tr);

                            var clazzName = $("#add_clazzList").combobox("getText");
                            var clazzTd = $("<td></td>");
                            var clazzInput = $("<input style='width: 200px; height: 30px;' data-options='readonly: true' class='easyui-textbox' name='clazzid' />").val(clazzName).attr("clazzId", clazzId);
                            $(clazzInput).appendTo(clazzTd);
                            $(clazzTd).appendTo(tr);

                            var courseName = $("#add_courseList").combobox("getText");
                            var courseTd = $("<td></td>");
                            var courseInput = $("<input style='width: 200px; height: 30px;' data-options='readonly: true' class='easyui-textbox' name='courseid' />").val(courseName).attr("courseId", courseId);
                            $(courseInput).appendTo(courseTd);
                            $(courseTd).appendTo(tr);

                            var removeTd = $("<td></td>");
                            var removeA = $("<a href='javascript:;' class='easyui-linkbutton removeBtn'></a>").attr("data-options", "iconCls:'icon-remove'");
                            $(removeA).appendTo(removeTd);
                            $(removeTd).appendTo(tr);

                            $(tr).appendTo(table);

                            //解析
                            $.parser.parse($(table).find(".chooseTr :last"));
                            //关闭窗口
                            $("#chooseCourseDialog").dialog("close");
                        }
                    }
                ]
            });

            //下拉框通用属性
            $("#add_gradeList, #add_clazzList, #add_courseList").combobox({
                width: "200",
                height: "30",
                multiple: false, //不可多选
                editable: false, //不可编辑
                method: "get",
            });

            $("#add_gradeList").combobox({
                url: "/s/grade?action=data&t="+new Date().getTime(),
                valueField: "gid",
                textField: "gradeName",
                onChange: function(newValue, oldValue){
                    //当年级的值发生改变的时候，同时修改班级和课程的下拉列表的值
                    //加载该年级下的班级
                    //清空班级的下拉列表
                    $("#add_clazzList").combobox("clear");
                    //给班级的下拉列表添加一个查询参数，gid
                    $("#add_clazzList").combobox("options").queryParams = {gid: newValue};
                    //重新加载班级的下拉列表
                    $("#add_clazzList").combobox("reload");

                    //加载该年级下的课程
                    $("#add_courseList").combobox("clear");
                    $("#add_courseList").combobox("options").queryParams = {gid: newValue};
                    $("#add_courseList").combobox("reload");
                },
                onLoadSuccess: function(){
                    //默认选择第一条数据
                    //获取年级的 json 数组 [{gid:1,gradeName:xxx},{}]
                    var data = $(this).combobox("getData");
                    //设置默认选中第一个年级
                    $(this).combobox("setValue", data[0].gid);
                }
            });

            //班级列表的下拉框
            //大家注意，我们在这里先不给班级设置 gid
            $("#add_clazzList").combobox({
                url: "/s/clazz?action=data_from_teacher",
                valueField: "cid",
                textField: "clazzName",
                onLoadSuccess: function(){
                    //默认选择第一条数据
                    var data = $(this).combobox("getData");
                    $(this).combobox("setValue", data[0].cid);
                }
            });

            $("#add_courseList").combobox({
                url: "/s/course?action=data_from_teacher&t="+new Date().getTime(),
                valueField: "cid",
                textField: "courseName",
                onLoadSuccess: function(){
                    //默认选择第一条数据
                    var data = $(this).combobox("getData");;
                    $(this).combobox("setValue", data[0].cid);
                }
            });

            //编辑教师信息
            $("#editDialog").dialog({
                title: "修改教师信息",
                width: 850,
                height: 550,
                iconCls: "icon-edit",
                modal: true,
                collapsible: false,
                minimizable: false,
                maximizable: false,
                draggable: true,
                closed: true,
                buttons: [
                    {
                        text:'设置课程',
                        plain: true,
                        iconCls:'icon-book-add',
                        handler:function(){
                            $("#chooseCourseDialog").dialog("open");
                        }
                    },
                    {
                        text:'提交',
                        plain: true,
                        iconCls:'icon-user_add',
                        handler:function(){
                            var validate = $("#editForm").form("validate");
                            if(!validate){
                                $.messager.alert("消息提醒","请检查你输入的数据!","warning");
                                return;
                            } else{
                                var chooseCourse = [];
                                $(table).find(".chooseTr").each(function(){
                                    var gradeid = $(this).find("input[textboxname='gradeid']").attr("gradeId");
                                    var clazzid = $(this).find("input[textboxname='clazzid']").attr("clazzId");
                                    var courseid = $(this).find("input[textboxname='courseid']").attr("courseId");
                                    var course = gradeid+"_"+clazzid+"_"+courseid;
                                    chooseCourse.push(course);
                                });
                                var tid = $("#dataList").datagrid("getSelected").tid;
                                var number = $("#edit_number").textbox("getText");
                                var name = $("#edit_name").textbox("getText");
                                var sex = $("#edit_sex").textbox("getText");
                                var phone = $("#edit_phone").textbox("getText");
                                var qq = $("#edit_qq").textbox("getText");
                                var data = {tid:tid, number:number, name:name,gender:sex,phone:phone,qq:qq,course:chooseCourse};

                                $.ajax({
                                    type: "put",
                                    url: "/s/teacher",
                                    data: JSON.stringify(data),//JSON.stringify(data) 函数表示将参数转为 json 字符串
                                    contentType: 'application/json',//设置请求头，浏览器&服务端都知道，提交的参数 是 json 格式
                                    success: function(msg){
                                        if(msg.status == 200){
                                            $.messager.alert("消息提醒","修改成功!","info");
                                            //关闭窗口
                                            $("#editDialog").dialog("close");
                                            //清空原表格数据
                                            $("#edit_number").textbox('setValue', "");
                                            $("#edit_name").textbox('setValue', "");
                                            $("#edit_sex").textbox('setValue', "男");
                                            $("#edit_phone").textbox('setValue', "");
                                            $("#edit_qq").textbox('setValue', "");
                                            $(table).find(".chooseTr").remove();

                                            //重新刷新页面数据
                                            $('#dataList').datagrid("reload");
                                            $('#dataList').datagrid("uncheckAll");

                                        } else{
                                            $.messager.alert("消息提醒",msg.msg,"warning");
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    },
                    {
                        text:'重置',
                        plain: true,
                        iconCls:'icon-reload',
                        handler:function(){
                            $("#edit_name").textbox('setValue', "");
                            $("#edit_phone").textbox('setValue', "");
                            $("#edit_qq").textbox('setValue', "");

                            $(table).find(".chooseTr").remove();

                        }
                    },
                ],
                //打开对话框之前的准备工作，这里打开的对话框不是空的，里边有数据，在 onBeforeOpen 方法中，把当前教师的信息填入对话框
                onBeforeOpen: function(){
                    var selectRow = $("#dataList").datagrid("getSelected");//{number:xx,name:xx}
                    //设置值
                    $("#edit_number").textbox('setValue', selectRow.number);
                    $("#edit_name").textbox('setValue', selectRow.name);
                    $("#edit_sex").textbox('setValue', selectRow.gender);
                    $("#edit_phone").textbox('setValue', selectRow.phone);
                    $("#edit_qq").textbox('setValue', selectRow.qq);
                    // $("#edit_photo").attr("src", "PhotoServlet?method=GetPhoto&type=3&number="+selectRow.number);

                    var courseList = selectRow.courses;

                    for(var i = 0;i < courseList.length;i++){
                        var gradeId = courseList[i].grade.gid;
                        var gradeName = courseList[i].grade.gradeName;
                        var clazzId = courseList[i].clazz.cid;
                        var clazzName = courseList[i].clazz.clazzName;
                        var courseId = courseList[i].course.cid;
                        var courseName = courseList[i].course.courseName;
                        //添加到表格显示
                        var tr = $("<tr class='chooseTr'><td>课程:</td></tr>");

                        var gradeTd = $("<td></td>");
                        var gradeInput = $("<input style='width: 200px; height: 30px;' data-options='readonly: true' class='easyui-textbox' name='gradeid' />").val(gradeName).attr("gradeId", gradeId);
                        $(gradeInput).appendTo(gradeTd);
                        $(gradeTd).appendTo(tr);

                        var clazzTd = $("<td></td>");
                        var clazzInput = $("<input style='width: 200px; height: 30px;' data-options='readonly: true' class='easyui-textbox' name='clazzid' />").val(clazzName).attr("clazzId", clazzId);
                        $(clazzInput).appendTo(clazzTd);
                        $(clazzTd).appendTo(tr);

                        var courseTd = $("<td></td>");
                        var courseInput = $("<input style='width: 200px; height: 30px;' data-options='readonly: true' class='easyui-textbox' name='courseid' />").val(courseName).attr("courseId", courseId);
                        $(courseInput).appendTo(courseTd);
                        $(courseTd).appendTo(tr);

                        var removeTd = $("<td></td>");
                        var removeA = $("<a href='javascript:;' class='easyui-linkbutton removeBtn'></a>").attr("data-options", "iconCls:'icon-remove'");
                        $(removeA).appendTo(removeTd);
                        $(removeTd).appendTo(tr);

                        $(tr).appendTo(table);

                        //解析
                        $.parser.parse($(table).find(".chooseTr :last"));

                    }

                },
                onClose: function(){
                    $("#edit_name").textbox('setValue', "");
                    $("#edit_phone").textbox('setValue', "");
                    $("#edit_qq").textbox('setValue', "");

                    $(table).find(".chooseTr").remove();
                }
            });

            // 一行选择课程
            $(".removeBtn").live("click", function(){
                $(this).parents(".chooseTr").remove();
            });

        });
    </script>
</head>
<body>
<!-- 数据列表 -->
<table id="dataList" cellspacing="0" cellpadding="0">

</table>
<!-- 工具栏 -->
<div id="toolbar">
    <div style="float: left;"><a id="add" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true">添加</a></div>
    <div style="float: left;" class="datagrid-btn-separator"></div>
    <div style="float: left;"><a id="edit" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true">修改</a></div>
    <div style="float: left;" class="datagrid-btn-separator"></div>
    <div><a id="delete" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-some-delete',plain:true">删除</a></div>
</div>

<!-- 添加窗口 -->
<div id="addDialog" style="padding: 10px;">
    <div style=" position: absolute; margin-left: 560px; width: 250px; height: 300px; border: 1px solid #EEF4FF" id="photo">
        <img alt="照片" style="max-width: 250px; max-height: 300px;" title="照片" src="photo/teacher.jpg" />
    </div>
    <form id="addForm" method="post">
        <table id="addTable" border=0 style="width:800px; table-layout:fixed;" cellpadding="6" >
            <tr>
                <td style="width:40px">学号:</td>
                <td colspan="3">
                    <input id="add_number"  class="easyui-textbox" style="width: 200px; height: 30px;" type="text" name="number" data-options="required:true, validType:'repeat', missingMessage:'请输入工号'" />
                </td>
                <td style="width:80px"></td>
            </tr>
            <tr>
                <td>姓名:</td>
                <td colspan="4"><input id="add_name" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="name" data-options="required:true, missingMessage:'请填写姓名'" /></td>
            </tr>
            <tr>
                <td>性别:</td>
                <td colspan="4"><select id="add_sex" class="easyui-combobox" data-options="editable: false, panelHeight: 50, width: 60, height: 30" name="sex"><option value="男">男</option><option value="女">女</option></select></td>
            </tr>
            <tr>
                <td>电话:</td>
                <td colspan="4"><input id="add_phone" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="phone" validType="mobile" /></td>
            </tr>
            <tr>
                <td>QQ:</td>
                <td colspan="4"><input id="add_qq" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="qq" validType="number" /></td>
            </tr>
        </table>
    </form>
</div>

<!-- 设置课程 -->
<div id="chooseCourseDialog" style="padding: 10px">
    <table cellpadding="8" >
        <tr>
            <td>年级：</td>
            <td><input id="add_gradeList" style="width: 200px; height: 30px;" class="easyui-combobox" name="gradeid" /></td>
        </tr>
        <tr>
            <td>班级：</td>
            <td><input id="add_clazzList" style="width: 200px; height: 30px;" class="easyui-combobox" name="clazzid" /></td>
        </tr>
        <tr>
            <td>课程：</td>
            <td><input id="add_courseList" style="width: 200px; height: 30px;" class="easyui-combobox" name="courseid" /></td>
        </tr>
    </table>
</div>

<!-- 修改窗口 -->
<div id="editDialog" style="padding: 10px">
    <div style=" position: absolute; margin-left: 560px; width: 250px; height: 300px; border: 1px solid #EEF4FF">
        <img id="edit_photo" alt="照片" style="max-width: 200px; max-height: 400px;" title="照片" src="" />
    </div>
    <form id="editForm" method="post">
        <table id="editTable" border=0 style="width:800px; table-layout:fixed;" cellpadding="6" >
            <tr>
                <td style="width:40px">工号:</td>
                <td colspan="3"><input id="edit_number" data-options="readonly: true" class="easyui-textbox" style="width: 200px; height: 30px;" type="text" name="number" data-options="required:true, validType:'repeat', missingMessage:'请输入工号'" /></td>
                <td style="width:80px"></td>
            </tr>
            <tr>
                <td>姓名:</td>
                <td><input id="edit_name" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="name" data-options="required:true, missingMessage:'请填写姓名'" /></td>
            </tr>
            <tr>
                <td>性别:</td>
                <td><select id="edit_sex" class="easyui-combobox" data-options="editable: false, panelHeight: 50, width: 60, height: 30" name="sex"><option value="男">男</option><option value="女">女</option></select></td>
            </tr>
            <tr>
                <td>电话:</td>
                <td><input id="edit_phone" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="phone" validType="mobile" /></td>
            </tr>
            <tr>
                <td>QQ:</td>
                <td colspan="4"><input id="edit_qq" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="qq" validType="number" /></td>
            </tr>
        </table>
    </form>
</div>


</body>
</html>