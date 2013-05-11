<a href="#list-entrada" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-entrada" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>					
						<g:sortableColumn property="folio" title="Folio" />					
						<g:sortableColumn property="fecha" title="Fecha" />					
						<g:sortableColumn property="idSalAlma" title="Almacen" />					
						<g:sortableColumn property="numeroFactura" title="Remision" />
						<g:sortableColumn property="usuario" title="Registro" />
						
						<g:if test="${almacen != 'F'}">
							<g:sortableColumn property="paqueteq" title="Paquete" />
							<g:sortableColumn property="area" title="Area" />						
						</g:if>
						
						<g:sortableColumn property="estado" title="Estado" />					
					</tr>
				</thead>
				<tbody>
				<g:each in="${entradaInstanceList}" status="i" var="entradaInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td>${fieldValue(bean: entradaInstance, field: "folio")}</td>					
						<td><g:link action="create" id="${entradaInstance.id}">
							<g:formatDate date="${entradaInstance.fecha}" format="dd/MM/yyyy" /></g:link></td>											
						<td>${fieldValue(bean: entradaInstance, field: "folioAlmacen")}</td>					
						<td>${fieldValue(bean: entradaInstance, field: "numeroFactura")}</td>					
						<td>${fieldValue(bean: entradaInstance, field: "usuario")}</td>
						
						<g:if test="${almacen != 'F'}">
							<td>${fieldValue(bean: entradaInstance, field: "paqueteq")}</td>					
							<td>${fieldValue(bean: entradaInstance, field: "area")}</td>						
						</g:if>
						
						
						<td>${fieldValue(bean: entradaInstance, field: "estado")=='A'?'ACTIVO':'CANCELADO'}</td>					
											
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${entradaInstanceTotal}" />
			</div>
		</div>