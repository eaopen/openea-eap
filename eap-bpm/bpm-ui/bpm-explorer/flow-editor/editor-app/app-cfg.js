/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
'use strict';

var ACTIVITI = ACTIVITI || {};

ACTIVITI.CONFIG = {
	'contextRoot' : '/bpm/api',
	//'contextRoot' : '/bpm-platform',
	//'contextRoot' : 'http://bpm-boot:8080',
};

var __ctx = '/bpm/api';
//var __ctx = '/bpm-platform';

// 跨域时修改 此 ctx http://localhost:8080

//var __ctx = 'http://bpm-boot:8080';


window.getCtxUrl = function(url,replaceRequestParam){
	if(url && !url.startWith(__ctx) && !url.startWith("http")){
		url.startWith("/")?"":url = "/"+url;
		url = __ctx + url;
		/*//如果URL含?且需要替换页面请求参数，则进行格式化
		if(replaceRequestParam && url.indexOf("?")!=-1){
			url = url.format(jQuery.getParams());
		}*/
	}
	return url;
}
//设计器跨域
jQuery(function () {  //, headers: { 'x-requested-with': 'XMLHttpRequest' }
	jQuery.ajaxSetup({crossDomain: true, xhrFields: {withCredentials: true}});
});