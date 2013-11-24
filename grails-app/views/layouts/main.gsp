<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">		
		<link rel='stylesheet' href="${resource(dir: 'css', file: 'jquery.dataTables.css')}" type="text/css"/> 
		<link rel="stylesheet" href="${resource(dir:'css',file:'ui.jqgrid.css')}" type="text/css" />
		<link rel="stylesheet" href="${resource(dir:'css',file:'jquery-ui-timepicker-addon.css')}" type="text/css" />
		
		<g:javascript library="jquery"/>
		<g:javascript library="jquery-ui"/>		
		<g:javascript library="application"/>
		
		<g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
	
		
		<g:javascript src="i18n/grid.locale-es.js"/>
		<g:javascript src="jquery.jqGrid.min.js"/>
		<g:javascript src="jquery.maskedinput.min.js"/>
		<g:javascript src="jquery.validate.min.js"/>
		<g:javascript src="jquery.currency.js"/>
		<g:javascript src="jquery.timer.js"/>
		<g:javascript src="jquery-ui-timepicker-addon.js"/>	
	
		<table>
			<tr>
				<td>
					<img src="${resource(dir: 'images', file: 'logotipo.jpg')}" alt="INR"/>
				</td>
				<td>
					<h3><g:message code="main.title.application"/></h3>
				</td>
				<td>
					<img src="${resource(dir: 'images', file: 'logoINR2013.png')}" alt="INR"/>
				</td>
			</tr>					
		</table>
		
		<div style="text-align: center">
			<sec:ifLoggedIn>	
				Inicio sesion como: <span style="color:blue"><g:usuarioActual/></span>
				<a href="${createLink(controller:'logout',action: 'index')}">Cerrar Sesion</a>	
			</sec:ifLoggedIn>
		</div>
		
		
		<div id="dialog-confirm"></div>
		<div id="dialog-mensaje"></div>
		
		<g:layoutBody/>
		
		<div class="footer" role="contentinfo"></div>
		<div id="spinner" class="spinner" style="display:none;">
		<g:message code="spinner.alt" default="Loading&hellip;"/>
		</div>
		
		
		
		<r:layoutResources />
	</body>
</html>
