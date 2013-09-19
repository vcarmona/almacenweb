$(document).ready(function() {	
	$("#folio").focus()
	
	$("#fecha").datepicker({
		dateFormat: 'dd/mm/yy',
		showButtonPanel: true,
		changeMonth: true,
		changeYear: true,
		
		onSelect: function () {	       
	        $("#folio").focus();
	    }
		
	});
	
	autoCompleteArticulo(function(){
		$("#cantidad").focus()
	});
	
	autoCompleteArea(function(){});
	
	consultarDetalle();
	detalleAdd();
	capturar();
	validar();	
	controlesDetalle()
	controlesHead()
	maskDates()
	
	
	capturarClave()
});


function capturarClave(){
	
	 $( "#capturaArticulo" ).dialog({
		 autoOpen: false,
		 height: 400,
		 width: 600,
		 modal: true,
		 buttons: {
		 "Guardar": function() {
			 var bValid = true;
			 $( this ).dialog( "close" );
		 },
		 "Cancelar": function() {
		 	( this ).dialog( "close" );
		 }
		 },
		 close: function() {
			 allFields.val( "" ).removeClass( "ui-state-error" );
		 }
		 });
	 
	 
		 $("#btnCapturaArticulo" ).button().click(function() {
			 $( "#capturaArticulo" ).dialog( "open" );
		 });
	
}



function validar(){
	$("#formPadre").validate({
		
		ignore: [],
		
        rules: {
                fecha: {required:true,validateDate:true,dateToday:true,checkCierre:true},                
                folio: {required:true, number:true, uniqueFolio:true},
                folioAlmacen:{number:true, uniqueFolioSalAlma:true},
                remision: {required: function() {
                    return $('#folioAlmacen').val() == undefined || $('#folioAlmacen').val() == '' ;
                }},               
                supervisa:{required:true},
                recibe:{required:true},                
                insumo: {required:true, number:true,checkInsumo:true},
                solicitado: {required:true,number:true},
                cantidad: {required:true,number:true},
                convertidoSolicitado: {required:true,number:true},
                convertido: {required:true,number:true},
                precio: {required:true, number:true},
                fechaCaducidad: {required:true,validateDate:true}
        },
		messages: {
				fecha : {required:"Requerido"},
				folio:{required:"Requerido",number:"Numerico"},
				//folioAlmacen:{numeric:"Requerido"},
				remision: {required:"Requerido"},				
				supervisa:{required:"Requerido"},
				recibe:{required:"Requerido"},
				insumo :{required:"Requerido", number:"Numerico"},
				solicitado:{required:"Requerido",number:"Numerico"},
				cantidad:{required:"Requerido",number:"Numerico"},
				convertidoSolicitado:{required:"Requerido",number:"Numerico"},
				convertido:{required:"Requerido",number:"Numerico"},
				precio: {required:"Requerido", number:"Numerico"},
				fechaCaducidad: {required:"Requerido"}
							
		}
  });
}

function capturar(){
	
	$("#fecha").keypress(function(e){	
		 if(e.which == 13) {
			$("#folio").focus()		
		 }
	});
	
	$("#folio").keypress(function(e){	
		 if(e.which == 13) {
			$("#remision").focus()		
		 }
	});
	
	/*$("#remision").focus(function(){		
		$(".busqueda").show()
	});*/
	
	$("#remision").keypress(function(e){	
		 if(e.which == 13) {
			 if($("#areaauto").val() != undefined)
					$("#areaauto").focus()
				else		
					$("#supervisa").focus()		
		 }
	});
	
	$("#remision").change(function(){
		if(this.value != ''){
			$("#folioAlmacen").val('');
			$("#folioAlmacen").prop('disabled', true);
		}
		else{
			$("#folioAlmacen").prop('disabled', false);
		}
	});
	
	var area = $("#areaauto")

	area.change( function() {
		if(area.val() == "")
			$("#cveArea").val("")
	});
	
	area.keypress(function(e){	
		 if(e.which == 13) {
			 $("#supervisa").focus()	
		 }
	});
	
	$("#supervisa").keypress(function(e){	
		 if(e.which == 13) {
			$("#recibe").focus()		
		 }
	});
	
	$("#recibe").keypress(function(e){	
		 if(e.which == 13) {
			$("#insumo").focus()		
		 }
	});
	
	
	$("#folioAlmacen").focus(function(){
		$("#detalle").clearGridData();
		$("#guardarAlmacen").hide()
		$(".busqueda").hide()
	});
	
	$("#folioAlmacen").keypress(function(e){	
		 if(e.which == 13) {
			 consultarDetalleMaterial();	
		 }
	});
	
	$("#folioAlmacen").change(function(){
		
		if(this.value != ''){
			$("#remision").val('');
			$("#remision").prop('disabled', true);
		}
		else{
			$("#remision").prop('disabled', false);
		}
	});
	
	
	$("#paqueteq").change(function(){
		
		if($("#paqueteq").val() == ''){
			$("#guardarPaquete").hide()
			$(".busqueda").show()
		}
		else{
			$("#guardarPaquete").show()
			$(".busqueda").hide()
		}
		
		consultarPaquete()				
	});
	
}

function consultarDetalle(){
	$("#detalle").jqGrid({
	    url: url +'/consultarDetalle',
	    datatype: 'json',
	    mtype: 'GET',
	    colNames:['Clave','Descripcion', 'U. Medida','Solicitado','Cantidad','Precio U.','Lote','F. Caducidad'],
	    colModel :[ 
	      {name:'cveArt', index:'cveArt', width:50, align:'center',editable:false,search:true,stype:'text'}, 
	      {name:'desArticulo', index:'desArticulo', width:500,editable:false},	      
	      {name:'unidad', index:'unidad', width:100,editable:false,align:'center'},
	      {name:'solicitado', index:'solicitado', width:80,editable:true,align:'center',hidden: true},
	      {name:'cantidad', index:'cantidad', width:80,editable:true,align:'center'},
	      {name:'precioEntrada', index:'precioEntrada', width:120,editable:true,align:'right',formatter: 'currency', formatoptions: { decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 4, prefix: "$", suffix:"", defaultValue: '0.00'}},
	      {name:'noLote', index:'noLote', width:100,editable:true,hidden: true},
	      {name:'fechaCaducidad', index:'fechaCaducidad', width:120,editable:true,sorttype:'date',align:'center',hidden: true}
	    ],
	    postData:{idPadre:function() { return $('#idPadre').val() }},
	    onSelectRow: function(id){	
		},		
		afterInsertRow:function (rowid, 
				rowdata,
				rowelem){
		},
	    pager: '#pager',
	    editurl: url + "/actualizarDetalle",
	    rowNum:20,
	    rowList:[20, 40, 60 ,80],
	    //sortname: 'id',
	    //sortorder: 'asc',
	    sortable: false,
	    viewrecords: true,
	    gridview: true,
	    caption: 'Entrada Detalle'			   
	})	
	
	
	
	
	
	
	$("#detalle").jqGrid("navGrid", "#pager",
			{
		add: false,
		edit: false,
		del: false,
		search: true,
		refresh: false
			},	
			{//edit
			},
			{},//add
			{//del
				savekey: [true, 13],
				width:500,
				closeOnEscape: true,
				reloadAfterSubmit:true,
				closeAfterSubmit:true,
				afterSubmit: function (data) {
					alert('prueb')
					return [true,'',''];
				}
			},	
			{},
			{});
	
	if(almacen == 'F'){		
		jQuery("#detalle").jqGrid('showCol',"noLote");
		jQuery("#detalle").jqGrid('showCol',"fechaCaducidad");
		jQuery("#detalle").setColProp('precioEntrada',{editable:true});
	}
	else{
		jQuery("#detalle").jqGrid('showCol',"solicitado");
		jQuery("#detalle").setColProp('precioEntrada',{editable:false});
		
		
	}
	
	$("#detalle").jqGrid('setGridWidth', 950);
	$("#detalle").jqGrid('setGridHeight', 250);
	
}

function controlesHead(){
	
	if($("#idPadre").val() != ''){		
		
		if($("#isAdmin").val()=='true' ||  $("#isDueno").val()=='true'){
			$(".botonOperacion").show()
			$(".busqueda").show()
		}
		
		if($("#estado").val()=='C' || $("#existeCierre").val()=='true'){
			$(".botonOperacion").hide()
			$(".busqueda").hide()
			$("#formPadre :input").each(function(){
				$(this).prop('disabled', true)
			});
			$("#imprimir").show()				
		}
		
		$("#paqueteq").prop('disabled', true)
	}
	
	if($("#paqueteq").val() != undefined && $("#paqueteq").val() !=''){
		$(".busqueda").hide()
	}

	//Es una actualizacion con folio almacen
	if($("#folioAlmacen").val() != undefined && $("#folioAlmacen").val() != ''){
		$(".busqueda").hide()
		$("#remision").prop('disabled', true);
		$("#folioAlmacen").prop('disabled', true);	
	}
	
	if($("#idPadre").val() != '' && $("#remision").val() != ''){
		$("#folioAlmacen").prop('disabled', true);
	}	
	
	$("#guardarAlmacen").click(function(){	
		if( confirm('Esta seguro de guardar el folio de almacen?') && 
		 $(".cabecera").valid() && $("#folio").valid() && $("#folioAlmacen").valid()){
			guardarTodo(this)
			$("#folioAlmacen").prop('disabled', true);
		}
	});
	
	$("#guardarPaquete").click(function(){	
		if(confirm('Esta seguro de guardar el paquete?') && 
			$(".cabecera").valid() && $("#folio").valid() && $("#remision").valid()){
			guardarTodo(this)
			$("#paqueteq").prop('disabled', true)
		}
	});
	
	$("#actualizar").click(function(){
		
		if(confirm('Esta seguro de actualizar la entrada?') && $(".cabecera").valid()){
			actualizar();
		}	
	})
	
	
	$("#cancelar").click(function(){
		if(confirm('Esta seguro de cancelar la entrada?')){
			cancelar()
		}
	})
	
}

function detalleAdd(){
	
	$("#insumo").focus(function(){	
		limpiarRenglonDetalle()
	});
	
	$("#insumo").keypress(function(e){	
		 if(e.which == 13 && $("#insumo").valid() ) {
			$.getJSON(url + "/buscarArticulo",{id:this.value})
					.done(function( json ) {
						 $("#artauto").val(json.desArticulo)						 
						 $("#unidad").val(json.unidad)
						 
						 if(almacen == 'F'){						 
							 $("#cantidad").focus()
						 }
						 else{
							 $("#solicitado").focus()
						 }
					})
					.fail(function() {
						alert("La clave " + $("#insumo").val() + " no existe")
						limpiarRenglonDetalle()
					})
		 }
	});
	
	$("#solicitado").keypress(function(e){	
		 if(e.which == 13) {
			 if(almacen != 'F'){
					$.getJSON(url + "/convertidora",{clave:$("#insumo").val(),cantidad:this.value})
						.done(function( json ) {							 
							 $("#precio").val(json.precio)
							 $("#convertidoSolicitado").val(json.cantidad)
							 $("#ualmacen").val(json.ualma)
							 $("#calmacen").val(json.calma)
							 $("#uceye").val(json.uceye)
							 $("#cceye").val(json.cceye)
							 $("#cociente").val(json.cociente)
							 
							 $("#cantidad").focus()
						})
			}
		 }
	});
	
	$("#cantidad").keypress(function(e){	
		 if(e.which == 13) {
			
			$("#precio").focus()
			
			if(almacen != 'F'){
				$.getJSON(url + "/convertidora",{clave:$("#insumo").val(),cantidad:this.value})
					.done(function( json ) {
						 $("#precio").val(json.precio)
						 $("#convertido").val(json.cantidad)
						 $("#ualmacen").val(json.ualma)
						 $("#calmacen").val(json.calma)
						 $("#uceye").val(json.uceye)
						 $("#cceye").val(json.cceye)
						 $("#cociente").val(json.cociente)
						 
						 $("#convertido").focus()
				})
			}
			 
					
		 }
	});
	
	$("#convertido").keypress(function(e){	
		 if(e.which == 13) {
			 agregar()			 
		 }
	});
	
	$("#precio").keypress(function(e){	
		 if(e.which == 13) {
			$("#noLote").focus()		
		 }
	});
	
	$("#noLote").keypress(function(e){	
		 if(e.which == 13) {
			$("#fechaCaducidad").focus()		
		 }
	});
	
	$("#fechaCaducidad").keypress(function(e){	
		 if(e.which == 13) {		 
			agregar()
		 }
	});
}

function agregar(){
	
	 if($("#formPadre").valid()){
		 
		 var cantidad = $("#cantidad").val()
		 
		 if($("#convertido").val() != null)
			 cantidad = $("#convertido").val()
		 
		 
		 
		 var data = [{	cveArt:$("#insumo").val(),
			 			solicitadoFarmacia:$("#solicitado").val(),
			 			cantidadFarmacia:$("#cantidad").val(),			 			
			 			solicitado:$("#convertidoSolicitado").val(),
			 			cantidad:cantidad,			 			
			 			precioEntrada:$("#precio").val(),
			 			noLote:$("#noLote").val(),
			 			fechaCaducidad:$("#fechaCaducidad").val()}];
		 
		 guardar(JSON.stringify(data))
		 
		 $("#clavelast").html($("#insumo").val());
		 $("#deslast").html($("#artauto").val());
		 $("#unidadlast").html($("#unidad").val());
		 
		 if(almacen != 'F'){
			 $("#solicitadolast").html($("#convertidoSolicitado").val());		
		 }
		 
		 $("#cantidadlast").html(cantidad);		 
		 $("#preciolast").html($("#precio").val());
		 $("#lotelast").html($("#noLote").val());
		 $("#caducidadlast").html($("#fechaCaducidad").val());	 			 
		
		 limpiarRenglonDetalle()
		 $("#insumo").focus()
		 $("#folioAlmacen").prop('disabled', true);
		
	 }
	
}


/////////////////FUNCIONES PROPIAS////////////

function maskDates(){
	/*$("#fechaCaducidad").datepicker({
		dateFormat: 'dd/mm/yy',
		showButtonPanel: true,
		changeMonth: true,
		changeYear: true});*/
}

function consultarDetalleMaterial(){
	
	$("#detalle").clearGridData();	
	
	$.getJSON( url + "/consultarDetalleMaterial",{folioAlmacen:$("#folioAlmacen").val()})
		.done(function( json ) {
	        for (var i = 0; i < json.rows.length; i++) {        	
	        	$("#detalle").addRowData(json.rows[i].cveArt, json.rows[i]);
	        }
	        
        $("#idSalAlma").val(json.idSalAlma)
	})	
	
	$("#registra").focus()
	$(".busqueda").hide()
	$("#guardarAlmacen").show()
}






















