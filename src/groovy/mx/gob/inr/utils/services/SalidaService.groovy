package mx.gob.inr.utils.services

import java.lang.reflect.Constructor
import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import mx.gob.inr.ceye.ArticuloCeye;
import mx.gob.inr.ceye.CostoPromedioCeye;
import mx.gob.inr.ceye.SalidaCeye
import mx.gob.inr.farmacia.SalidaFarmacia;
import mx.gob.inr.utils.AutoCompleteService;
import mx.gob.inr.utils.Cie09;
import mx.gob.inr.utils.Paciente;
import mx.gob.inr.seguridad.Usuario;
import mx.gob.inr.utils.UtilService;
import mx.gob.inr.utils.domain.Articulo
import mx.gob.inr.utils.domain.Salida;

abstract class SalidaService<S extends Salida> implements IOperacionService<S> {
	
	
	public UtilService utilService
	public EntradaService entradaService
	public AutoCompleteService autoCompleteService
	
	protected entitySalida
	protected entitySalidaDetalle
	protected entityEntradaDetalle
	protected entityArticulo
	protected entityArea
	protected entityCierre
	
	public SalidaService(entitySalida, entitySalidaDetalle,
		 entityEntradaDetalle, entityArticulo, entityArea, entityCierre){
		this.entitySalida = entitySalida
		this.entitySalidaDetalle = entitySalidaDetalle	
		this.entityEntradaDetalle = entityEntradaDetalle
		this.entityArticulo = entityArticulo
		this.entityArea = entityArea
		this.entityCierre = entityCierre				
	}
		 
    @Override
	S setJson(jsonSalida, String ip, Usuario usuarioRegistro,String almacen){		
		
		def salida = entitySalida.newInstance()
		salida.almacen = almacen
		salida.ipTerminal = ip				
		salida.folio = jsonSalida.folio as int
		salida.fecha = new Date().parse("dd/MM/yyyy",jsonSalida.fecha)
		
		salida.jefeServicio = jsonSalida.autorizaauto
		salida.recibio  = jsonSalida.recibeauto
		salida.entrego = Usuario.get(jsonSalida.entrega)
		salida.area = entityArea.get(jsonSalida.cveArea)
		salida.noOrden = null
		salida.usuario = usuarioRegistro
		salida.paciente = Paciente.get(jsonSalida.idPaciente)
		
		if(salida instanceof SalidaFarmacia){
			salida.tipoSolicitud = jsonSalida.tipoSolicitud
			salida.horaEntrega = new Date().parse("HH:mm",jsonSalida.horaEntrega)		
		}
		else if(salida instanceof SalidaCeye){
			salida.diagnostico =  Cie09.get(jsonSalida.idProcedimiento)
			salida.nosala = jsonSalida.nosala?jsonSalida.nosala as short:null
			salida.paqueteq = jsonSalida.paqueteq
			salida.tipoVale = jsonSalida.tipoVale
		}
				
		return salida
	}
	
	@Override
	S guardar(S salida){				
		salida.save([validate:false])
		return salida		
	}
	
	/***
	 * 
	 * @param jsonDetalle
	 * @param salida
	 * @param renglon
	 * @return renglon + 1 
	 */
	@Override
	def guardarDetalle(jsonDetalle, S salida, Integer renglon,String almacen ){
		
		Long clave = jsonDetalle.cveArt as long 
		Double solicitado = jsonDetalle.solicitado as double
		Double surtido = jsonDetalle.surtido as double
		
		//println ("jsonValor " + jsonDetalle.costo)
		
		def entradas = cargarEntradasDeSalida(clave, salida.fecha, almacen, surtido)
		
		entradas.each(){
			
			def articulo = entityArticulo.get(clave)			
			def salidaDetalle = entitySalidaDetalle.newInstance()
			
			salidaDetalle.salida = salida			
			
			if(!renglon)
				salidaDetalle.renglon = consecutivoRenglon(salida)
			else
				salidaDetalle.renglon = renglon++
			
			//salidaDetalle.entrada = it.entrada
			//salidaDetalle.renglonEntrada = it.renglon
			salidaDetalle.articulo = articulo
			salidaDetalle.cantidadPedida = solicitado
			salidaDetalle.cantidadSurtida = it.restarExistencia
			salidaDetalle.fechaCaducidad = it.fechaCaducidad
			salidaDetalle.noLote = it.noLote
			
			salidaDetalle.precioUnitario = costoPromedio(articulo, almacen)			
			
			salidaDetalle.presupuesto = null
			salidaDetalle.actividad = null
			salidaDetalle.entradaDetalle = entityEntradaDetalle.get (it.id)
			
			salidaDetalle.save([validate:false])
			
			it.existencia -=  it.restarExistencia
			
			if(it.id)
				it.save([validate:false])
		}
		
		return renglon
		
		
	}
	
	
	@Override
	def guardarTodo(S salida, jsonArrayDetalle,String almacen){		
				
		for(jsonDetalle in jsonArrayDetalle){			
			Long clave = jsonDetalle.cveArt as long
			Double surtido = jsonDetalle.surtido as double
			def disponible = disponibilidadArticulo(clave,salida.fecha,almacen)
			
			if(surtido > disponible){
				return "Existencia insuficiente clave $clave"
			}
		}
		
		salida = guardar(salida)
		
		Integer renglon = 1
		
		jsonArrayDetalle.each() {
			renglon = guardarDetalle(it, salida, renglon,almacen)
		}
		
		return "Salida Guardada"
		
	}
	
	@Override
	@Transactional
	def String actualizar(S salida, Long idSalidaUpdate,String almacen){		
	
	 def salidaUpdate = entitySalida.get(idSalidaUpdate);
	 
	 def mensaje = "Salida actualizada"
	 
	 if(salida.folio != salidaUpdate.folio ){
		 if (checkFolio(salida.folio,almacen)){
			 mensaje = "Folio no actualizable, ya existe";
			 salida.folio = salidaUpdate.folio
		 }
	 }
	 
	 /**Si cambio la fecha de salida, se regresan existencias y vuelve a insertar */
	 if(salida.fecha.compareTo(salidaUpdate.fecha) < 0){
		 
		 def detalleList = entitySalidaDetalle.createCriteria().list(){
			 projections{
				 groupProperty("articulo")
				 sum("cantidadSurtida")
				 groupProperty("cantidadPedida")
			 }
			 
			 eq("salida.id", idSalidaUpdate)
			 order('articulo', 'asc')
		 }		 
		 
		 for(detalle in detalleList){
			 Long clave = detalle[0].id
			 Double surtido = detalle[1]
			 
			 borrarDetalle(idSalidaUpdate, clave)//delete			 	
			 def disponible = disponibilidadArticulo(clave,salida.fecha,almacen)
			 
			 if(surtido > disponible){
				 def fechaFormat = salida.fecha.format("dd/MM/yyyy")
				 throw new RuntimeException("A la fecha $fechaFormat la existencia es:$disponible Clave:$clave")//Rollback				 
			 } 
		 }		 
		 
		 Integer renglon = 1		 
		 detalleList.each(){
			 def clave = it[0].id
			 def jsonDetalle = [cveArt: clave,solicitado:it[2],surtido:it[1]]
			 //borrarDetalle(idSalidaUpdate, clave)
			 renglon = guardarDetalle(jsonDetalle, salidaUpdate, renglon,almacen)			 
		 }		 
	 }
	 
	 salidaUpdate.properties = salida.properties;
	 salidaUpdate.save([validate:false])
	 
	 return mensaje;
		
	}
	
	@Override
	def consultar(Long idSalida){
		
	}	
	
	@Override
	def cancelar(Long idSalida, String almacen){
		
		def salida =  entitySalida.get(idSalida);
		regresarExistencias(salida)

		salida.estado = 'C'
		salida.save([validate:false])
		return "Cancelado"
	}
	
	@Override
	def listar(params, Usuario usuarioLogueado){
		
		def sortIndex = params.sort ?: 'folio'
		def sortOrder  = params.order ?: 'desc'
		
		def fechas = utilService.fechasAnioActual()
				
		def salidaList = entitySalida.createCriteria().list(params){
			
			between("fecha",fechas.fechaInicio,fechas.fechaFin)
			eq("almacen",params.almacen)			
			order(sortIndex, sortOrder)
		}.each(){
		
			it.dueno = usuarioLogueado == it.usuario
		}
		
		def salidaTotal = entitySalida.createCriteria().get{
			projections{
				count()
			}			
			between("fecha",fechas.fechaInicio,fechas.fechaFin)
			eq("almacen",params.almacen)
		}
		
		[lista:salidaList, total:salidaTotal]
		
	}
	
	@Override
	def actualizarDetalle(Long idSalida,params, String almacen){	
		
		def clave  = params.long('id')
		def solicitado = params.double('solicitado')
		def surtido =params.double('surtido')
		
		def sumaSurtido = entitySalidaDetalle.createCriteria().get{			
			projections{
				sum("cantidadSurtida")
			}			
			eq('salida.id', idSalida)
			eq("articulo.id", clave)			
		}
		
		def salidaUpdate = entitySalida.get(idSalida)
		
		if(solicitado == null || solicitado < 0.0){
			return "Solicitado Invalido"
		}
		
		if(surtido ==null || surtido < 0.0){
			return "Surtido Invalido"
		}	
		
		
		def disponible = disponibilidadArticulo(clave, salidaUpdate.fecha,almacen) + (sumaSurtido - surtido)
		
		if(disponible < 0){
			return "Cantidad Disponible $disponible"
		}
		
		
		borrarDetalle(idSalida, clave)		
		def jsonDetalle=[cveArt:clave, solicitado:solicitado,surtido:surtido]		
		guardarDetalle(jsonDetalle,salidaUpdate,null,almacen)
		return "success"
		
	}
	
	@Override
	@Transactional
	def borrarDetalle(Long idSalida, Long clave, String almacen=null){
		
		def detalle = entitySalidaDetalle.createCriteria().list(){
			eq('salida.id', idSalida)
			eq("articulo.id", clave)
		}
		
		detalle.each(){
			def entradaDetalle  = it.entradaDetalle
			
			if(entradaDetalle){
				entradaDetalle.existencia += it.cantidadSurtida
				entradaDetalle.save([validate:false])
			}
			
			it.delete([validate:false])
		}
		
		return "success"
		
	}
	
	@Override
	def consultarDetalle(params){
		///def sortIndex = params.sidx ?: 'id'
		///def sortOrder  = params.sord ?: 'asc'
		//def maxRows = Integer.valueOf(params.rows)
		def currentPage = 1
		//def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def idSalida  = params.long('idPadre')
		
		
		//Para la busqueda
		def searchOper = params.searchOper
		def searchString = params.searchString
		def searchField = params.searchField
		def search = params._search
		def searchClave = ""		
		if(search == 'true'){
			if(searchOper == 'eq' && searchField == 'cveArt'){
				if(searchString)
					searchClave = " and sd.articulo.id = $searchString "
			}
			 
		}
		
		def entitySalidaDetalleName = entitySalidaDetalle.name
		
		def query =
		"""\
			select sd 
			from $entitySalidaDetalleName sd 
			join fetch sd.salida  
			join fetch sd.articulo			 			
			where sd.salida.id = $idSalida  
			$searchClave				
			order by sd.renglon asc

		"""
		
		
				
		def detalle = entitySalidaDetalle.executeQuery(query,[])		
		
		def results = []
		
		detalle.each(){ sd->
			
			def renglon = results.find { r -> r.id == sd.articulo.id }
			
			if(renglon){				
				renglon.cell[6] += sd.cantidadSurtida				
			}
			else{
				results << [cell:[sd.articulo.id, sd.articulo.desArticulo?.trim(), 
					sd.articulo.unidad?.trim(), sd.precioUnitario, disponibilidadArticulo(sd.articulo.id, sd.salida.fecha,params.almacen),
					 sd.cantidadPedida, sd.cantidadSurtida], id: sd.articulo.id]
			}		
			
		}
		
		//def detalleCount = results.size()
		def totalRows = results.size();
		def numberOfPages = 1
		
		
		def jsonData = [rows: results, page: currentPage, records: totalRows, total: numberOfPages]
		
		return jsonData
		
	}
			
	private def regresarExistencias(S salida){
		
		def salidasDetalle = entitySalidaDetalle.createCriteria().list(){
			eq("salida", salida)
		}
		
		salidasDetalle.each(){
			def entradaDetalle  = it.entradaDetalle
			
			if(entradaDetalle){
				entradaDetalle.existencia += it.cantidadSurtida
				entradaDetalle.save([validate:false])
			}
		}
		
	}
	
	private def cargarEntradasDeSalida(Long clave, Date fecha, String almacen, Double surtido){
		
		def entradas = entradaService.entradasDetalle(clave, fecha, almacen)
				
		log.info("Num entradas " + entradas.size())
		
		def entradasAfectadas = afectarEntradas(entradas, surtido);
		
		log.info("Num entradas2 " + entradasAfectadas.size())
		
		if(entradasAfectadas.size() == 0){
			
			//def entradaDetalle = new entityEntradaDetalle();	
			def entradaDetalle = entityEntradaDetalle.newInstance();
			
			entradaDetalle.entrada = null
			entradaDetalle.articulo = entityArticulo.get(clave)
			entradaDetalle.cantidad = 0.0
			entradaDetalle.existencia = 0.0
			entradaDetalle.precioEntrada = 0.0
			entradaDetalle.fechaCaducidad = null
			entradaDetalle.noLote = null
			entradaDetalle.restarExistencia = 0.0		

			
			entradasAfectadas.add(entradaDetalle);
		}
		
		entradasAfectadas
	}
		
	private def afectarEntradas(entradas, surtido){
		def despachado = 0.0;
		
		def result = [];
		
		if(surtido == 0){
			return result
		}
		
		for(entradaDetalle in entradas){
			despachado = entradaDetalle.existencia - surtido;
			
			log.info("Despachado " + despachado)
			
			if(despachado < 0){
				entradaDetalle.restarExistencia = entradaDetalle.existencia
				result.add(entradaDetalle)
				surtido = Math.abs(despachado)
			}
			else if(despachado >=0){
				entradaDetalle.restarExistencia = surtido
				result.add(entradaDetalle)
				break
			}
			
		}
		
		return result
	}
	
	@Override
	def consecutivoRenglon(S salida){
		utilService.consecutivoRenglon(entitySalidaDetalle,"salida",salida)
	}
	
	@Override
	def checkFolio(Integer folio,String almacen){		
		utilService.checkFolio(entitySalida, folio,almacen)		
	}
	
	@Override
	def consecutivoFolio(String almacen){
		utilService.consecutivoFolio(entitySalida,almacen)
	}
	
	@Override
	def usuarios(Long idPerfil){
		return utilService.usuarios(idPerfil)		
	}
	
	@Override
	@Transactional(readOnly=true)
	def buscarArticulo(Long id, String almacen){
		def articulo = entityArticulo.get(id)
		articulo.desArticulo = articulo.desArticulo.trim()
		
		if(articulo instanceof ArticuloCeye){				
			articulo.movimientoProm = costoPromedio(articulo,almacen)
		}	
		return articulo
	}
	
	/****
	 * Obtiene el costo promedio del almacen de ceye
	 */
	@Transactional(readOnly=true)
	def costoPromedio(Articulo articulo, String almacen){
		utilService.getMovimientoPromedio(articulo, almacen)
	}

	@Override
	def listarArticulo(String term){
		autoCompleteService.listarArticulo(term, entityArticulo)
	}

	@Override
	def listarArea(String term){
		autoCompleteService.listarArea(term, entityArea)
	}
	
	@Override
	def checkCierre(Date fecha, String almacen){
		utilService.checkCierre(entityCierre, fecha, almacen)
	}
	
	@Override
	def reporte(Long id,String almacen){
		
		def entitySalidaDetalleName = entitySalidaDetalle.name
		
		def diagnostico = ""
		
		if(almacen != 'F')
			diagnostico = " left join fetch s.diagnostico "
					
		def query =
		"""
			select sd from $entitySalidaDetalleName sd
			left join fetch sd.salida s 
			left join fetch s.entrego 
			left join fetch s.paciente 
			left join fetch s.area 
			$diagnostico
			left join fetch sd.articulo art 
			where s.id = $id 
			order by sd.renglon asc
		"""	
		
		def detalleList = entitySalidaDetalle.executeQuery(query,[])
		
		return detalleList
		
	}
	
	def listarRecibe(String term){
		autoCompleteService.listarNombre(entitySalida, "recibio", term)
	}
	
	
	def listarAutoriza(String term){
		autoCompleteService.listarNombre(entitySalida, "jefeServicio", term)
	}
	
	def listarProcedimiento(String term){
		autoCompleteService.listarProcedimiento(term)
	}
	
	
	def disponibilidadArticulo(Long clave, Date fecha,String almacen){		
		return entradaService.disponibilidadArticulo(clave, fecha, almacen)
	}
	
}
