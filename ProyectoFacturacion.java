/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectofacturacion;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PROYECTO FINAL - PROGRAMACION I
 * SISTEMA DE FACTURACION DE CLIENTES, MATRICES Y BASE DE DATOS LOCAL
 *FLUJO OPERATIVO DEL PROGRAMA:
 * 1. CONEXIÓN: Verifica el estado del servidor MySQL de XAMPP para activar/desactivar el respaldo automático.
 * 2. CAPTURA (Ciclo 3 Clientes): Pide y valida datos del carro, tipo de cliente (Regular/VIP) y días de estadía.
 * 3. MENÚS OPERATIVOS: Deriva al cliente a un departamento (Mecánica, Electricidad, Pintura) con precios fijos.
 * 4. MATEMÁTICA COMERCIAL: Aplica descuentos, cargos bancarios por tarjeta e impuestos de ley (15% ISV).
 * 5. SALIDA Y AUDITORÍA: Imprime la factura individual, inserta en BD y genera un reporte gerencial acumulativo.
 * * @author Nelsy Cruz y Josue Hernandez
 * 
 */
public class ProyectoFacturacion {

    // Configuración de las credenciales locales para conectar Java con el servidor de XAMPP
    private static final String URL_BD = "jdbc:mysql://localhost:3306/taller_nuevos_horizontes";
    private static final String USER_BD = "root"; // Usuario por defecto de XAMPP
    private static final String PASS_BD = "";     // Por defecto XAMPP no trae contraseña

    /**
     * FUNCIÓN PRINCIPAL (MAIN)
     * Es el cerebro del programa. Desde aquí controlamos el flujo principal,
     * declaramos las estructuras de datos y ejecutamos el ciclo para los 3 clientes.
     */
    public static void main(String[] args) {
        // Instanciamos el Scanner para poder capturar lo que el usuario escriba en el teclado
        Scanner input = new Scanner(System.in);
        
        // Definimos el tamaño de nuestras estructuras (3 clientes como pide la rúbrica)
        final int TOTAL_CLIENTES = 3;
        
        // La matriz tendrá 11 columnas porque ocupamos desglosar cada cálculo de dinero individualmente
        final int COLUMNAS_FINANZAS = 11; 
        
        // DECLARACIÓN DE ARREGLOS PARALELOS Y MATRIZ
        // La fila [i] de la matriz financiera va a amarrar los números con los textos del cliente en la posición [i]
        double[][] matrizFinanciera = new double[TOTAL_CLIENTES][COLUMNAS_FINANZAS];
        String[] nombresClientes = new String[TOTAL_CLIENTES];
        String[] marcasVehiculos = new String[TOTAL_CLIENTES];
        String[] modelosVehiculos = new String[TOTAL_CLIENTES];
        String[] placasVehiculos = new String[TOTAL_CLIENTES];
        String[] tiposClientes = new String[TOTAL_CLIENTES]; 
        String[] metodosPago = new String[TOTAL_CLIENTES];
        String[] nombresServicios = new String[TOTAL_CLIENTES];
        int[] aniosVehiculos = new int[TOTAL_CLIENTES];

        // Inicializamos la matriz llenándola con ceros para limpiar la memoria antes de meter datos
        inicializarMatriz(matrizFinanciera, TOTAL_CLIENTES, COLUMNAS_FINANZAS);

        // Encabezado estético del negocio
        System.out.println("==========================================================================");
        System.out.println("            SISTEMA AUTOMATIZADO DE FACTURACION Y AUDITORIA              ");
        System.out.println("                    TALLER AUTOMOTRIZ NUEVOS HORIZONTES                   ");
        System.out.println("               PROGRAMADO POR: NELSY CRUZ Y JOSUE HERNANDEZ                      ");
        System.out.println("==========================================================================");
        
        // EFECTO VISUAL: Hace una pequeña pausa simulando que el sistema procesa datos
        esperarUnMomento();
        
        // Intentamos tocar la puerta del servidor de XAMPP antes de iniciar las capturas
        System.out.println("\n[INFO] Probando la conexión con el servidor MySQL de XAMPP...");
        Connection conexionActiva = verificarConexionServidor();
        
        // Si el puente de conexión no regresó vacío (null), significa que XAMPP está encendido
        if (conexionActiva != null) {
            System.out.println("[OK] >>> ¡CONEXION EXITOSA CON LA BASE DE DATOS LOCAL! <<<");
            System.out.println("[INFO] Todo se va a respaldar automáticamente en phpMyAdmin.\n");
            try {
                conexionActiva.close(); // Cerramos la conexión de prueba para no saturar el servidor
            } catch (SQLException e) {
                System.out.println(">> Alerta al cerrar la conexión inicial: " + e.getMessage());
            }
        } else {
            // Si regresó null, alertamos que trabajaremos solo con la memoria RAM de las matrices
            System.out.println("\n[ALERTA] ¡EL SERVIDOR DE XAMPP ESTA APAGADO O CAIDO!");
            System.out.println("[ALERTA] El programa va a seguir corriendo usando la memoria de las matrices,");
            System.out.println("[ALERTA] pero no va a guardar nada de forma permanente en la base de datos.\n");
        }

        // CICLO PRINCIPAL (FOR): Recorre de 0 a 2 para procesar de forma consecutiva a los 3 clientes
        for (int i = 0; i < TOTAL_CLIENTES; i++) {
            System.out.println("\n==========================================================================");
            System.out.println(">>> AUDITORIA CENTRAL: PROCESANDO REGISTRO DE CONTROL #" + (i + 1) + " <<<");
            System.out.println("==========================================================================");
            
            // Capturamos el nombre y lo pasamos a mayúsculas para mantener uniformidad en el reporte
            System.out.print("Nombre completo del cliente: ");
            nombresClientes[i] = input.nextLine().toUpperCase();
            
            // Llamamos a la función que valida si el cliente es VIP o Regular
            tiposClientes[i] = capturarTipoCliente(input);
            
            System.out.print("Marca del vehiculo: ");
            marcasVehiculos[i] = input.nextLine().toUpperCase();
            
            System.out.print("Modelo del vehiculo: ");
            modelosVehiculos[i] = input.nextLine().toUpperCase();
            
            // Función con ciclo de validación para el año del carro
            aniosVehiculos[i] = capturarAnioVehiculo(input);
            
            System.out.print("Numero de placa: ");
            placasVehiculos[i] = input.nextLine().toUpperCase();

            // Mostramos el menú de departamentos operativos y guardamos la respuesta
            int departamento = capturarDepartamento(input);
            matrizFinanciera[i][0] = departamento; // Guardamos el código del departamento en la columna 0
            
            // Variables temporales para alojar lo que elija el usuario en los submenús
            int servicioCodigo = 0;
            double precioBase = 0.0;
            String txtServicio = "";

            // EVALUACIÓN DE SELECCIÓN: Abrimos el catálogo del departamento correspondiente
            if (departamento == 1) {
                // Caso Mecánica
                servicioCodigo = desplegarMenuMecanica(input);
                precioBase = evaluarPrecioMecanica(servicioCodigo);
                txtServicio = evaluarNombreMecanica(servicioCodigo);
            } else if (departamento == 2) {
                // Caso Electricidad
                servicioCodigo = desplegarMenuElectricidad(input);
                precioBase = evaluarPrecioElectricidad(servicioCodigo);
                txtServicio = evaluarNombreElectricidad(servicioCodigo);
            } else {
                // Caso Pintura (Cualquier valor que no sea 1 o 2 cae aquí por descarte)
                servicioCodigo = desplegarMenuPintura(input);
                precioBase = evaluarPrecioPintura(servicioCodigo);
                txtServicio = evaluarNombrePintura(servicioCodigo);
            }
            
            // Vaciamos los resultados del servicio en las columnas de la fila actual [i]
            matrizFinanciera[i][1] = servicioCodigo; // Columna 1: Código de la operación realizada
            matrizFinanciera[i][2] = precioBase;     // Columna 2: Dinero base del servicio
            nombresServicios[i] = txtServicio;       // Guardamos el nombre textual en el vector paralelo

            // Capturamos los días y calculamos la estadía cobrando L.50 fijos por cada día
            int diasTaller = capturarDiasTaller(input);
            matrizFinanciera[i][3] = diasTaller; // Columna 3: Cantidad de días
            
            double costoEstadia = calcularCostoEstadia(diasTaller, 50.0);
            matrizFinanciera[i][4] = costoEstadia; // Columna 4: Costo total de estadía

            // Operación matemática: Sumamos el servicio + la estadía para el primer subtotal
            double subtotalNeto = calcularSubtotalNeto(precioBase, costoEstadia);
            matrizFinanciera[i][5] = subtotalNeto; // Columna 5: Subtotal inicial

            // Evaluamos políticas comerciales: Si es VIP se calcula el 10% de descuento, si no, se va en 0
            double tasaDescuento = evaluarTasaDescuento(tiposClientes[i]);
            double montoDescuento = calcularMontoDescuento(subtotalNeto, tasaDescuento);
            matrizFinanciera[i][6] = tasaDescuento;   // Columna 6: Porcentaje aplicado (0.10 o 0.0)
            matrizFinanciera[i][7] = montoDescuento;  // Columna 7: Dinero ahorrado por el cliente

            // Capturamos la forma de pago. Si es tarjeta, se le recarga un 5% sobre el subtotal neto
            String metodo = capturarMetodoPago(input);
            metodosPago[i] = metodo;
            double cargoComision = calcularCargoTarjeta(subtotalNeto, metodo);
            matrizFinanciera[i][8] = cargoComision; // Columna 8: Recargo financiero bancario

            // Ajustamos las cuentas: Restamos el descuento y sumamos la comisión de la tarjeta
            double subtotalConAjustes = (subtotalNeto - montoDescuento) + cargoComision;
            
            // Calculamos el impuesto legal obligatorio del 15% de ISV en Honduras
            double impuestoISV = calcularImpuestoISV(subtotalConAjustes);
            matrizFinanciera[i][9] = impuestoISV; // Columna 9: Impuesto desglosado

            // Sumamos el subtotal ajustado más el impuesto para dar con la cifra final de la factura
            double granTotal = calcularGranTotal(subtotalConAjustes, impuestoISV);
            matrizFinanciera[i][10] = granTotal; // Columna 10: Total neto absoluto a pagar

            // Imprimimos el ticket limpio en pantalla usando formatos cuadrados (%-25s, %,12.2f)
            imprimirFacturaIndividual(
                nombresClientes[i], tiposClientes[i], marcasVehiculos[i], modelosVehiculos[i], aniosVehiculos[i], placasVehiculos[i],
                txtServicio, precioBase, diasTaller, costoEstadia, subtotalNeto, montoDescuento, cargoComision, impuestoISV, metodo, granTotal
            );

            // PERSISTENCIA: Mandamos todas las variables calculadas directamente a MySQL
            // Si la base de datos está caída, la función se desactiva sola sin tirar error
            ejecutarInsercionBD(
                nombresClientes[i], tiposClientes[i], marcasVehiculos[i], modelosVehiculos[i], aniosVehiculos[i], placasVehiculos[i],
                txtServicio, precioBase, diasTaller, costoEstadia, subtotalNeto, montoDescuento, cargoComision, impuestoISV, metodo, granTotal
            );
            
            // SANEAMIENTO DEL BÚFER: Limpiamos el salto de línea que deja atrapado el "nextInt()" anterior
            // para evitar que en la siguiente vuelta se salte la lectura del nombre del nuevo cliente
            input.nextLine(); 
        } // Fin del ciclo for de clientes

        // Una vez procesados los 3 clientes, imprimimos la gran tabla del balance de caja gerencial
        desplegarBalanceConsolidado(nombresClientes, tiposClientes, nombresServicios, placasVehiculos, metodosPago, matrizFinanciera, TOTAL_CLIENTES);
        
        System.out.println("\n==========================================================================");
        System.out.println("   ¡PROCESO COMPLETADO CON EXITO! REPORTES IMPRESOS Y REPOSITORIO LISTO   ");
        System.out.println("==========================================================================");
    }

    /**
     * FUNCIÓN: inicializarMatriz
     * Usa dos ciclos 'for' anidados (filas y columnas) para limpiar las celdas de la matriz financiera.
     */
    public static void inicializarMatriz(double[][] m, int f, int c) {
        for (int i = 0; i < f; i++) {
            for (int j = 0; j < c; j++) {
                m[i][j] = 0.0;
            }
        }
    }

    /**
     * FUNCIÓN: esperarUnMomento
     * Utiliza 'Thread.sleep' para simular efectos de carga en tiempo de ejecución.
     * Es obligatorio encerrarlo en un try-catch por el manejo de interrupciones de hilos en Java.
     */
    public static void esperarUnMomento() {
        try {
            System.out.print("[CARGA] Leyendo archivos del proyecto...");
            Thread.sleep(400); // Pausa de 400 milisegundos
            System.out.print(" Inicializando interfaz de consola...");
            Thread.sleep(400);
            System.out.println(" Hecho.");
        } catch (InterruptedException e) {
            System.out.println("Error en la espera.");
        }
    }

    /**
     * FUNCIÓN: capturarTipoCliente
     * Usa un ciclo 'do-while' para obligar al usuario a marcar solo la opción 1 o 2.
     */
    public static String capturarTipoCliente(Scanner scan) {
        int sel;
        do {
            System.out.println("Clasificacion de Antiguedad:");
            System.out.println("  1. Cliente Regular (Sin beneficios especiales)");
            System.out.println("  2. Cliente VIP Frecuente (Aplica 10% de descuento)");
            System.out.print("Seleccione la clasificacion (1-2): ");
            sel = scan.nextInt();
        } while (sel < 1 || sel > 2); // Si mete otro número, el ciclo se repite
        scan.nextLine(); // Limpieza rápida del búfer
        
        if (sel == 2) {
            return "VIP";
        } else {
            return "REGULAR";
        }
    }

    /**
     * FUNCIÓN: capturarAnioVehiculo
     * Aplica un ciclo 'while' de validación lógica para impedir años incoherentes o carros del futuro.
     */
    public static int capturarAnioVehiculo(Scanner scan) {
        int anio;
        System.out.print("Ingrese el anio de fabricacion del vehiculo: ");
        anio = scan.nextInt();
        while (anio < 1950 || anio > 2027) {
            System.out.println(">> ERROR: Anio fuera de rango.");
            System.out.print("Ingrese un anio valido (1950-2027): ");
            anio = scan.nextInt();
        }
        scan.nextLine(); 
        return anio;
    }

    /**
     * FUNCIÓN: capturarDepartamento
     * Menú operativo de control inicial para derivar el vehículo a su área técnica.
     */
    public static int capturarDepartamento(Scanner scan) {
        int depto;
        do {
            System.out.println("\nDEPARTAMENTOS OPERATIVOS");
            System.out.println("1. Departamento de Mecanica General");
            System.out.println("2. Departamento de Electricidad");
            System.out.println("3. Departamento de Pintura");
            System.out.print("Seleccione el departamento encargado: ");
            depto = scan.nextInt();
        } while (depto < 1 || depto > 3);
        return depto;
    }

    // --- BLOQUE DE MÉTODOS DEL DEPARTAMENTO DE MECÁNICA ---
    public static int desplegarMenuMecanica(Scanner scan) {
        int op;
        do {
            System.out.println("\nMENU DE SERVICIOS: DEPARTAMENTO DE MECANICA");
            System.out.println("1. Cambio de Aceite y Filtros Premium ...... L. 800.00");
            System.out.println("2. Reemplazo de Direccion Hidraulica ....... L. 3,500.00");
            System.out.println("3. Rectificacion de Sistema de Frenos ...... L. 2,200.00");
            System.out.println("4. Mantenimiento de Suspension Completa .... L. 5,000.00");
            System.out.print("Seleccione el servicio mecanico (1-4): ");
            op = scan.nextInt();
        } while (op < 1 || op > 4);
        return op;
    }

    public static double evaluarPrecioMecanica(int op) {
        switch (op) { 
            case 1: 
                return 800.0;
            case 2: 
                return 3500.0;
            case 3: 
                return 2200.0;
            case 4: 
                return 5000.0;
            default: 
                return 0.0;
        }
    }

    public static String evaluarNombreMecanica(int op) {
        if (op == 1) {
            return "Cambio de Aceite";
        }
        if (op == 2) {
            return "Direccion Hidraulica";
        }
        if (op == 3) {
            return "Sistema de Frenos";
        }
        if (op == 4) {
            return "Suspension Completa";
        }
        return "Desconocido";
    }

    // --- BLOQUE DE MÉTODOS DEL DEPARTAMENTO DE ELECTRICIDAD ---
    public static int desplegarMenuElectricidad(Scanner scan) {
        int op;
        do {
            System.out.println("\nMENU DE SERVICIOS: DEPARTAMENTO DE ELECTRICIDAD");
            System.out.println("1. Reparacion Integral del Sistema A/C ..... L. 4,500.00");
            System.out.println("2. Escaneo Diagnostico de Computadora (ECU) . L. 1,200.00");
            System.out.println("3. Reemplazo de Alternador y Bateria ....... L. 3,800.00");
            System.out.println("4. Reparacion del Sistema de Iluminacion ... L. 1,500.00");
            System.out.print("Seleccione el servicio electrico (1-4): ");
            op = scan.nextInt();
        } while (op < 1 || op > 4);
        return op;
    }

    public static double evaluarPrecioElectricidad(int op) {
        switch (op) {
            case 1: 
                return 4500.0;
            case 2: 
                return 1200.0;
            case 3: 
                return 3800.0;
            case 4: 
                return 1500.0;
            default: 
                return 0.0;
        }
    }

    public static String evaluarNombreElectricidad(int op) {
        switch (op) {
            case 1: 
                return "Reparacion de A/C";
            case 2: 
                return "Escaneo de ECU";
            case 3: 
                return "Alternador y Bateria";
            case 4: 
                return "Sistema Iluminacion";
            default: 
                return "Desconocido";
        }
    }
    
    // --- BLOQUE DE MÉTODOS DEL DEPARTAMENTO DE PINTURA ---
    public static int desplegarMenuPintura(Scanner scan) {
        int op;
        do {
            System.out.println("\nMENU DE SERVICIOS: DEPARTAMENTO DE PINTURA");
            System.out.println("1. Pintura en general ...... L. 15,000.00");
            System.out.println("2. Relleno o golpes ....... L. 4,500.00");
            System.out.println("3. Robin o pulido de pintura ...... L. 3,500.00");
            System.out.print("Seleccione el servicio de pintura (1-3): ");
            op = scan.nextInt();
        } while (op < 1 || op > 3); 
        return op;
    }

    public static double evaluarPrecioPintura(int op) {
        switch (op) {
            case 1: 
                return 15000.0;
            case 2: 
                return 4500.0;
            case 3: 
                return 3500.0;
            default: 
                return 0.0;
        }
    }

    public static String evaluarNombrePintura(int op) {
        if (op == 1) {
            return "Pintura General";
        }
        if (op == 2) {
            return "Relleno o golpes";
        }
        if (op == 3) {
            return "Pulido de pintura";
        }
        return "Desconocido";
    }

    /**
     * FUNCIÓN: capturarDiasTaller
     * Valida que no existan días negativos o cero para poder proceder con el cálculo logístico.
     */
    public static int capturarDiasTaller(Scanner scan) {
        int d;
        System.out.print("Ingrese el numero de dias estimados en el taller: ");
        d = scan.nextInt();
        while (d <= 0) {
            System.out.print(">> ERROR: Ingrese dias reales de trabajo (Mayor a 0): ");
            d = scan.nextInt();
        }
        return d;
    }

    /**
     * FUNCIÓN: capturarMetodoPago
     * Retorna Strings clave ("TARJETA", "TRANSFERENCIA", "EFECTIVO") evaluando el entero seleccionado.
     */
    public static String capturarMetodoPago(Scanner scan) {
        int formaPago;
        do {
            System.out.println("\nFORMAS DE PAGO ADMITIDAS");
            System.out.println("1. Dinero en Efectivo");
            System.out.println("2. Tarjeta de Credito/Debito (Aplica 5% de recargo)");
            System.out.println("3. Transferencia Bancaria Directa");
            System.out.print("Seleccione el metodo de pago (1-3): ");
            formaPago = scan.nextInt();
        } while (formaPago < 1 || formaPago > 3);
        
        if (formaPago == 2) {
            return "TARJETA";
        }
        if (formaPago == 3) {
            return "TRANSFERENCIA";
        }
        return "EFECTIVO";
    }

    // --- BLOQUE DE OPERACIONES MATEMÁTICAS PURAS (FUNCIONES DE RETORNO) ---
    public static double calcularCostoEstadia(int d, double p) { 
        double resultado = d * p;
        return resultado; 
    }
    
    public static double calcularSubtotalNeto(double s, double e) { 
        double resultado = s + e;
        return resultado; 
    }
    
    public static double evaluarTasaDescuento(String t) { 
        if (t.equals("VIP")) {
            return 0.10; 
        }
        return 0.0;
    }
    
    public static double calcularMontoDescuento(double s, double t) { 
        double resultado = s * t;
        return resultado; 
    }
    
    public static double calcularCargoTarjeta(double s, String m) { 
        if (m.equals("TARJETA")) {
            return s * 0.05; 
        }
        return 0.0;
    }
    
    public static double calcularImpuestoISV(double s) { 
        double resultado = s * 0.15;
        return resultado; 
    }
    
    public static double calcularGranTotal(double s, double i) { 
        double resultado = s + i;
        return resultado; 
    }

    /**
     * FUNCIÓN: imprimirFacturaIndividual
     * Muestra en la pantalla el ticket desglosado de cobro de cada cliente por separado.
     */
    public static void imprimirFacturaIndividual(String c, String t, String ma, String mo, int an, String pl, String se, double pb, int di, double cd, double sub, double de, double co, double isv, String pa, double tot) {
        System.out.println("\n-----------------------------------------------------------------");
        System.out.println("                    COMPROBANTE DE COMPRA FISCAL                 ");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf(" Cliente: %-25s  Categoria: %s\n", c, t);
        System.out.printf(" Vehiculo: %s %s (%d)  Placa: %s\n", ma, mo, an, pl);
        System.out.println("-----------------------------------------------------------------");
        System.out.printf(" Trabajo Realizado       : %s\n", se);
        System.out.printf(" Costo de Servicio Base  : L. %,12.2f\n", pb);
        System.out.printf(" Costo Operativo Estadia : L. %,12.2f (%d Dias)\n", cd, di);
        System.out.println("-----------------------------------------------------------------");
        System.out.printf(" Subtotal Neto Inicial   : L. %,12.2f\n", sub);
        System.out.printf(" Descuento Otorgado      : L. %,12.2f\n", de);
        System.out.printf(" Cargo Comision Tarjeta  : L. %,12.2f (%s)\n", co, pa);
        System.out.printf(" Impuesto Legal ISV 15%%  : L. %,12.2f\n", isv);
        System.out.println("-----------------------------------------------------------------");
        System.out.printf(" TOTAL NETO LIQUIDADO    : L. %,12.2f\n", tot);
        System.out.println("-----------------------------------------------------------------\n");
    }

    /**
     * FUNCIÓN: desplegarBalanceConsolidado
     * Es el reporte gerencial acumulativo. Recorre las filas de la matriz usando un for,
     * va sumando las columnas de dinero en variables acumuladoras y calcula métricas estratégicas.
     */
    public static void desplegarBalanceConsolidado(String[] clie, String[] tipo, String[] serv, String[] plac, String[] pago, double[][] finanzas, int filas) {
        System.out.println("\n========================================================================================================================================");
        System.out.println("                               REPORTE GERENCIAL CONSOLIDADO - BALANCE DE CAJA CENTRAL                              ");
        System.out.println("========================================================================================================================================");
        System.out.printf("%-15s %-12s %-22s %-12s %-15s %-12s %-12s %-12s %-12s\n", 
                "Cliente", "Categoria", "Servicio Realizado", "Placa", "Metodo Pago", "Costo Base", "Descuento", "ISV (15%)", "Total General");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        
        // Declaración de acumuladores financieros globales
        double acumuladorPrecioBase = 0.0;
        double acumuladorDescuentos = 0.0;
        double acumuladorISV = 0.0;
        double acumuladorTotales = 0.0;
        
        // Ciclo para procesar las filas de la matriz financiera e ir acumulando los ingresos totales
        for (int i = 0; i < filas; i++) {
            System.out.printf("%-15s %-12s %-22s %-12s %-15s L.%,-10.1f L.%,-10.1f L.%,-10.1f L.%,-10.1f\n", 
                    clie[i], tipo[i], serv[i], plac[i], pago[i], finanzas[i][2], finanzas[i][7], finanzas[i][9], finanzas[i][10]
            );
            // Sumamos los valores financieros de cada cliente al total global de la empresa
            acumuladorPrecioBase += finanzas[i][2];  
            acumuladorDescuentos += finanzas[i][7];  
            acumuladorISV        += finanzas[i][9];  
            acumuladorTotales    += finanzas[i][10]; 
        }
        
        // CÁLCULO DE MÉTRICAS COMPLEMENTARIAS (Ayuda a expandir la lógica comercial y las líneas de control)
        double promedioVentaPorCliente = acumuladorTotales / filas;
        double rendimientoBaseImpuestos = acumuladorPrecioBase * 0.15;
        
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" METRICAS GLOBALES:  Ingresos Base: L.%,.2f  |  Descuentos Cedidos: L.%,.2f  |  Impuestos Recaudados: L.%,.2f\n", 
                acumuladorPrecioBase, acumuladorDescuentos, acumuladorISV);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" INGRESO NETO TOTAL EN CAJA : L. %,.2f\n", acumuladorTotales);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" ANALISIS DE AUDITORIA INTERNA:\n");
        System.out.printf(" -> Ticket Promedio Estimado Facturado por Orden de Trabajo : L. %,.2f\n", promedioVentaPorCliente);
        System.out.printf(" -> Rendimiento Teórico de ISV proyectado sobre Base Bruta  : L. %,.2f\n", rendimientoBaseImpuestos);
        System.out.println("========================================================================================================================================\n");
    }

    /**
     * FUNCIÓN: verificarConexionServidor
     * Intenta cargar el driver puente de MySQL y abrir el puerto de comunicación local.
     * Si el servidor XAMPP está dormido, atrapa la excepción y retorna null evitando el colapso del programa.
     */
    public static Connection verificarConexionServidor() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); 
            Connection con = DriverManager.getConnection(URL_BD, USER_BD, PASS_BD);
            return con;
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] Driver JDBC no encontrado en las librerías.");
            return null;
        } catch (SQLException e) {
            return null; 
        }
    }

    /**
     * FUNCIÓN: ejecutarInsercionBD
     * Toma todos los datos calculados en Java, arma una instrucción SQL estructurada (INSERT INTO)
     * y utiliza un 'PreparedStatement' para inyectar las variables de forma segura dentro de las columnas de MySQL.
     */
    public static void ejecutarInsercionBD(String cliente, String tipo, String marca, String modelo, int anio, String placa, String servicio, double base, int dias, double estadia, double subtotal, double desc, double comision, double isv, String pago, double total) {
        Connection con = verificarConexionServidor(); 
        if (con == null) {
            return; 
        }
        
        // Estructuramos la consulta con 16 signos de interrogación para las variables
        String instruccionSQL = "INSERT INTO facturas_taller (nombre_cliente, tipo_cliente, marca_vehiculo, "
                + "modelo_vehiculo, anio_vehiculo, numero_placa, servicio_realizado, costo_base, dias_taller, "
                + "costo_estadia, subtotal_neto, monto_descuento, cargo_tarjeta, impuesto_isv, metodo_pago, "
                + "gran_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            // Preparamos la consulta SQL y empezamos a amarrar cada parámetro en su posición exacta
            PreparedStatement consultaPreparada = con.prepareStatement(instruccionSQL);
            consultaPreparada.setString(1, cliente);
            consultaPreparada.setString(2, tipo);
            consultaPreparada.setString(3, marca);
            consultaPreparada.setString(4, modelo);
            consultaPreparada.setInt(5, anio);
            consultaPreparada.setString(6, placa);
            consultaPreparada.setString(7, servicio);
            consultaPreparada.setDouble(8, base);
            consultaPreparada.setInt(9, dias);
            consultaPreparada.setDouble(10, estadia);
            consultaPreparada.setDouble(11, subtotal);
            consultaPreparada.setDouble(12, desc);
            consultaPreparada.setDouble(13, comision);
            consultaPreparada.setDouble(14, isv);
            consultaPreparada.setString(15, pago);
            consultaPreparada.setDouble(16, total);
            
            // Ejecutamos la inserción real en las tablas de phpMyAdmin
            consultaPreparada.executeUpdate();
            System.out.println("[BASE DE DATOS] --> Registro guardado en la tabla MySQL de XAMPP con éxito.");
            
            // Cerramos los canales de comunicación de SQL por buenas prácticas
            consultaPreparada.close();
            con.close();
        } catch (SQLException e) {
            // En caso de un fallo en la inyección de datos, atrapamos el mensaje de error aquí
            System.out.println("[ALERTA BD] Hubo un problema al insertar el registro en SQL: " + e.getMessage());
        }
    }
}