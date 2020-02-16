    /***************变量声明*****************/     
    var pageSize = 10; 	//默认每页数据量            
    var currentPage = 1; //当前页码            
    var start = 1;		//查询的页码
   	var tableData = [],taskGroupList=[];
   	var jobTableCtl;
   	
   	$.extend({ alert: function (message, title) {
		  $("<div></div>").dialog( {
		    buttons: { "确定": function () { $(this).dialog("close"); } },
		    close: function (event, ui) { $(this).remove(); },
		    resizable: false,
		    title: title,
		    modal: true
		  }).text(message);
		}
	});

   	$.extend({ confirm: function (message, title,callback) {   			
   		
		  $("<div></div>").dialog({
			    buttons: { "确定": function () {
				    	callback();
				    	$(this).dialog("close"); 
			    	},
			    	"取消": function () {
				    	$(this).dialog("close"); 
				    	}
			    },
			    close: function (event, ui) { $(this).remove(); },
			    resizable: false,
			    title: title,
			    modal: true
			  }).text(message);
			
   			}
		});
        
    function handleOperate(oper,rowData){
    	var operType,urlString;
    	switch(oper){
        	case 'pause':
        	 	operType = "PUT";
        	 	urlString = "job?pause";
        		break;
        	case 'continue':
        		operType = "PUT";
        		urlString = "job?resume";
        		break;
        	case 'modify':
        		operType = "PUT";
        		urlString = "job?cron";
        		break;
        	case 'delete':
        		operType = "DELETE";
        		urlString = "job";
        		break;
        	default:
        		alert(oper + " " + data);
        		break;
        }
    	
    	if('modify' != oper){
    		$.ajax({
                type: operType,
                url: urlString,
                data: {
                   "taskName": rowData.jobName,
                   "jobGroupName": rowData.jobGroup,
                   "cronExpression": rowData.cronExpression,
                   "command" : rowData.triggerGroup
   			 },
                dataType: "json",
                success: function(data){
                     jobTableCtl.ajax.reload();
                     updateGroupNameList();            	  
                },
   			error: function(XMLHttpRequest, textStatus, errorThrown){
   					// 状态码  
                       console.log(XMLHttpRequest.status);  
                       // 状态  
                       console.log(XMLHttpRequest.readyState);  
                       // 错误信息     
                       alert(textStatus);  		   
   				}
            });
    	}
    	else{
    		$("#form\\.jobName").val(rowData.jobName);
    		$("#form\\.jobGroup").val(rowData.jobGroup);
    		$("#form\\.cronExpression").val(rowData.cronExpression);
    		$("#form\\.description").val(rowData.description);
    		$("#form\\.command").val(rowData.triggerGroup);
    		$("#form\\.jobGroup").attr("disabled",true);
    		$("#form\\.description").attr("disabled",true);
    		$("#form\\.jobName").attr("disabled",true);
    		$("#form\\.command").attr("disabled",true);
    		$('#addTaskDlg').dialog({title:'修改定时计划'}).dialog('open');
    	}    
    }
    
    function updateGroupNameList(){
    	//初始化任务数据
        $.ajax({
             type: "GET",
             url: "job?taskGroupList",
             data:{},
             dataType: "json",
             success: function(data){			             	
                taskGroupList = data.data.data;                             
             },
			error: function(XMLHttpRequest, textStatus, errorThrown){								
                   console.log(XMLHttpRequest.status);  // 状态码  			                   
                   console.log(XMLHttpRequest.readyState);   // 状态  			                    
                   console.log(textStatus);  // 错误信息     
                   
                   $.alert(XMLHttpRequest.responseJSON.message, "错误");	   
			}
       });
    }
    
    $(document).ready(function() {
	    $( "#tabs" ).tabs();
	    $( "#radioset" ).buttonset();
	    
	    $( "#button-search" ).button({
	    	icon: "ui-icon-search",
			showLabel: true
	    });
		$( "#button-addnew" ).button({
			icon: "ui-icon-plusthick",
			showLabel: true
		});
		
		$( "#button-addnew" ).click(function( event ) {
			$('#addTaskDlg').dialog({title:'添加任务'}).dialog('open');
			
			$("#form\\.jobName").attr("disabled",false);
			$("#form\\.jobGroup").attr("disabled",false);
    		$("#form\\.description").attr("disabled",false);
    		$("#form\\.command").attr("disabled",false);
    		$("#form\\.jobName").focus();
			event.preventDefault();
		});
		
		$( "#button-search" ).click(function( event ) {
			$.confirm("这是一个错误信息", "删除");
		});
		
		
		$( "#addTaskDlg" ).dialog({
			autoOpen: false,	
			width: 400,
			height: 450,
			modal: true,
			buttons: [
				{
					text: "确定",
					click: function() {
						$(this).submit();       //执行提交方法
						$('#addTaskDlg span').removeClass('ui-icon ui-icon-check');
						$(this).dialog( "close" );						
					}
				},
				{
					text: "取消",
					click: function() {
						$('#addTaskDlg span').removeClass('ui-icon ui-icon-check');
						$(this).dialog( "close" );
						$('#addTaskDlg')[0].reset();
					}
				}
			]
		}).validate({           //验证表单
	            submitHandler: function (form) { //全部验证成功准备提交		
	            	var url = "job";
	            	var netType = "POST";   
	            	var isModify = $('#addTaskDlg').dialog('option', 'title') == "修改定时计划" ?  true : false;
	            	if(isModify){
	            		url = "job?cron";
	            		netType = "PUT";
	            	}
	            	
					$.ajax({
			             type: netType,
			             url: url,
			             data:{
			             	"taskName": $("#form\\.jobName").val().trim(),
			             	"jobGroupName": $("#form\\.jobGroup").val().trim(),
		                    "cronExpression": $("#form\\.cronExpression").val().trim(),
		                    "description": $("#form\\.description").val().trim(),
		                    "command": $("#form\\.command").val().trim()
						 },
			             dataType: "json",
			             success: function(data){			             	
			                jobTableCtl.ajax.reload();			                
			                $('#addTaskDlg')[0].reset();
							updateGroupNameList();
			             },
						error: function(XMLHttpRequest, textStatus, errorThrown){								
		                    console.log(XMLHttpRequest.status);  // 状态码  			                   
		                    console.log(XMLHttpRequest.readyState);   // 状态  			                    
		                    console.log(textStatus);  // 错误信息     
		                    
		                    $.alert(XMLHttpRequest.responseJSON.message, "错误")	;	   
						}
			       });		       
	            },
	            highlight: function (element, errorClass) {                             //如果出错，错误框边框改变颜色
	                $(element).css('border', '1px solid #f00');
	            },
	            unhighlight: function (element, errorClass) {                           //如果正确取消边框改变颜色
	                $(element).css('border', '1px solid #ccc');
	                $(element).parent().find('span').html('&nbsp;').addClass('ui-icon ui-icon-check');   //显示正确图片
	            },
	            showErrors: function (errorMap, errorList) {                            //获取出错句柄
	                var errors = this.numberOfInvalids();
	                if (errors > 0) {                                                   //判断出错句柄个数来改变对话框的高度
	                    $('#addTaskDlg').dialog('option', 'height', errors * 20 + 450);
	                } else {
	                    $('#addTaskDlg').dialog('option', 'height', 450);
	                }
	                this.defaultShowErrors();
	            },
	            errorLabelContainer: 'ol.reg_error',                                    //有错误时，将所有表单错误信息统一放到class为ol.reg_error的元素里
	            wrapper: 'li',                                                          //将每个错误信息放入一个li标签
	            rules : {
	                "form.jobName" : {
	                    required:true,
	                    rangelength:[2,20],
	                    remote:{
			                 async:false,
			                 url: "job?checkExist",     //后台处理程序
			                 type: "post",               //数据发送方式
			                 dataType: "json",           //接受数据格式   
			                 data: {                     //要传递的数据
			                     taskName: function() { return $('#form\\.jobName').val().trim();}
			                 }
			        	}
	                },
	                "form.command":{
	                    required:true,
	                    rangelength:[2,200]
	                },
	                "form.cronExpression":{
	                    required:true,
	                   	rangelength:[13,200]
	                }
	            },
	            messages : {
	                "form.jobName" : {
	                    required:'任务名不能为空！',
	                    rangelength:$.validator.format('用户名不得小于{0}位，大于{1}位'),
	                    remote : '任务名不能重复！'
	                },
	                "form.command":{
	                    required:'定时命令行不能为空',
	                    rangelength:$.validator.format('密码不得小于{0}位，大于{1}位')
	                    
	                },
	                "form.cronExpression":{
	                    required:'cron表达式不能为空',
	                    rangelength:'cron表达式格式不正确'
	                }
	            }
	        });
	    
	    
	    
	    jobTableCtl = $('#jobTable').DataTable({
             "language": {
                 "lengthMenu": "每页 _MENU_ 条记录",
                 "zeroRecords": "没有找到记录",
                 "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
                 "infoEmpty": "无记录",
                 "infoFiltered": "(从 _MAX_ 条记录过滤)",
                 "search" : "搜索:",
                 "paginate" : {
                    "first" : "首页",
                    "previous" : "上页",
                    "next" : "下页",
                    "last" : "末页"
                }
             },
              serverSide: true,
			  "ajax": {
				    "url": 'job',
				    "type": 'GET',
				    /*"dataSrc": "data.data",*/
				    "data": function ( d ) {
				    	if(d.order != null && d.order[0].column != null){
				        	d.sortFeild = d.columns[d.order[0].column].data;
				        	d.sortDir = d.order[0].dir;
				        }
				        if(d.search != null && '' != d.search.value){
				        	d.searchWord = d.search.value;
				        }
				    },
				    "dataFilter": function(data){//服务器返回数据处理
			            var json = jQuery.parseJSON( data );
			            var jsonObj = {};
			            jsonObj.recordsTotal = json.data.total;
			            jsonObj.recordsFiltered = json.data.total;
			            jsonObj.data = json.data.data;
			            jsonObj.draw  = json.data.draw;
			            return JSON.stringify(jsonObj); // return JSON string
			        }
				  },
			  "columns": [
		            { "data": "jobName" },
		            { "data": "jobGroup" },
		            { "data": "triggerGroup" },
		            { "data": "cronExpression" },
		            { "data": "description","width":"300px"},
		            { "data": "triggerState" },
		            { "data": null }
		     ],
		     "columnDefs": [
		    	 { "targets": [0,1,5],  "orderable": true},
		         { "targets": '_all', "orderable": false},
		    	 {"targets": 4,
			       "render" : function(data, type, row, meta){
			       		return '<div style="width:300px;  white-space: nowrap; overflow: hidden;text-overflow: ellipsis;" '+ 'title="'+data +'">'+data +
			       		'</div>';
		          }
			  },{
			      "targets": 5,
			       "render" : function(data, type, row, meta){
			       
		                if (row.triggerState === 'WAITING') {
		                    return "等待";
		                } else if (row.triggerState === 'PAUSED') {
		                    return "暂停";
		                } else if (row.triggerState === 'ACQUIRED') {
		                    return "正常执行";
		                } else if (row.triggerState === 'BLOCKED') {
		                    return "阻塞";
		                } else if (row.triggerState === 'ERROR') {
		                    return "错误";
		                } else{
		                	return "未知状态";
		                }
		          }
			  },{
		            "targets": 6,		           
		            "data": null,
		            "defaultContent": "<button class='button white small' style='font-size: 11px;' value='pause'>暂停</button><button class='button green small' value='continue' style='font-size: 11px;'>恢复</button><button class='button blue small' style='font-size: 11px;' value='modify'>修改</button><button class='button orange small' style='font-size: 11px;' value='delete'>删除</button>"
		      }	      
			]
         });
         
	     $('#jobTable tbody').on( 'click', 'button', function () {
	        var data = jobTableCtl.row($(this).parents('tr')).data();
	        var oper =$(this).attr("value");
	        if('delete' == oper){
	        	var message = "确实要删除" + data.jobName + "任务吗？";
	        	$.confirm(message, "删除",function(){
	        		$.ajax({
	                    type: "DELETE",
	                    url: "job",
	                    data: {
	                       "taskName": data.jobName,
	                       "jobGroupName": data.jobGroup,
	                       "cronExpression": data.cronExpression,
	                       "command" : data.triggerGroup
	       			 },
	                    dataType: "json",
	                    success: function(data){
	                         jobTableCtl.ajax.reload();
	                         updateGroupNameList();            	  
	                    },
	       			error: function(XMLHttpRequest, textStatus, errorThrown){
	       					// 状态码  
	                           console.log(XMLHttpRequest.status);  
	                           // 状态  
	                           console.log(XMLHttpRequest.readyState);  
	                           // 错误信息     
	                           alert(textStatus);  		   
	       				}
	                });
	        	});
	        }
	        else{
	        	handleOperate(oper,data);	        	
	        }
	    }); 	    
	    
	    //更新组名列表信息
        updateGroupNameList();
        	
       
        //任务分组自动补全
       $("#form\\.jobGroup").autocomplete({
            autoFocus: true,
            delay: 0,            
            source: function (request, response) {
                var hosts = taskGroupList, //起始
                    term = request.term,        //获取输入值
                    name = term,                //用户名
                    result = [];                //结果
                //结果第一条是自己输入
                result.push(term);                
                if (name) {
                    //得到找到的域名
                    var findedHosts = $.grep(hosts, function (value, index) {
                            return value.indexOf(name) > -1;
                        });
                    //增加一个自我输入
                    result = result.concat(findedHosts);
                }
                response(result);
            }
        });   
        
        
        
         
         
    //end 初始化
	});