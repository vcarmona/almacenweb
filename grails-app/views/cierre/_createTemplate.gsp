<style>
.progress-label {
	float: left;
	margin-left: 50%;
	margin-top: 5px;
	font-weight: bold;
	text-shadow: 1px 1px 0 #fff;
}
</style>

<g:javascript src="validaciones.js"/>
<g:javascript src="comunes.js"/>
<g:javascript src="cierre.js" />

<a href="#create-cierreFarmacia" class="skip" tabindex="-1"><g:message
		code="default.link.skip.label" default="Skip to content&hellip;" /></a>
<div class="nav" role="navigation">
	<ul>		
		<li>
			<g:link class="list" action="list">	<g:message code="default.list.label" args="[entityName]" /></g:link>
		</li>
		<li>
			<a href="${createLink(action: 'create')}" class="nuevo">Nuevo</a>
		</li>
	</ul>
</div>

<h1>
	<g:almacenDescripcion code="default.create.cierre.label" almacen="${almacen}"/>		
</h1>

<form id="formPadre">
	<table>
		<tr>
			<td><label for="fechaCierre">Fecha Cierre</label> <g:textField
					name="fechaCierre" size="8"
					value="${cierreInstance?.fechaCierre?.format('dd/MM/yyyy')}" />
			</td>
		

			<sec:noAccess expression="hasRole('ROLE_FARMACIA_LECTURA')">
				<td><input type="button" id="generar" name="generar" value="Generar Cierre" /></td>		
			</sec:noAccess>
	
		</tr>

	</table>
</form>

<div id="progressbar">
	<div class="progress-label">...</div>
</div>

