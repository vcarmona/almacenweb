package mx.gob.inr.farmacia

import java.io.Serializable;

import mx.gob.inr.utils.domain.EntradaDetalle;

class EntradaDetalleFarmacia extends EntradaDetalle {

	static auditable = true
	
	EntradaFarmacia entrada	
	ArticuloFarmacia articulo	
	
	
	static belongsTo = [entrada:EntradaFarmacia]
	
	static transients = ['restarExistencia']

    static mapping = {
		
		entrada column:'id_entrada'
		articulo column :'cve_art'
		renglon column:'renglon_entrada'
		
		version false
		table 'entrada_detalle'
		id column:'id_entradadetalle'
		id generator:'sequence' ,params:[sequence:'sq_identradadetallefarmacia']
		
		importe formula: "cantidad * precio_entrada"
		
		//id composite: ['entrada','renglonEntrada']
    }
	
//	static constraints = {
//		cantidad nullable:false, min:0.0
//		precioEntrada nullable:false, min:0.0
//	}
	
	
}