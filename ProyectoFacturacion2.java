/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectofacturacion2;

import java.util.Scanner;

/**
 * PROYECTO FINAL - ETAPA 2: MATRICES Y LOGICA GERENCIAL
 * @author Nelsy Cruz
 */
public class ProyectoFacturacion2 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        final int TOTAL_CLIENTES = 3;
        
        // Aquí defino que mi matriz va a tener 11 columnas para guardar bien desglosado todo el dinero
        final int COLUMNAS_FINANZAS = 11; 
        
        // Creo la matriz y los arreglos para guardar los datos de los 3 clientes sin que se borren
        double[][] matrizFinanciera = new double[TOTAL_CLIENTES][COLUMNAS_FINANZAS];
        String[] nombresClientes = new String[TOTAL_CLIENTES];
        String[] marcasVehiculos = new String[TOTAL_CLIENTES];
        String[] modelosVehiculos = new String[TOTAL_CLIENTES];
        String[] placasVehiculos = new String[TOTAL_CLIENTES];
        String[] tiposClientes = new String[TOTAL_CLIENTES]; 
        String[] metodosPago = new String[TOTAL_CLIENTES];
        String[] nombresServicios = new String[TOTAL_CLIENTES];
        int[] aniosVehiculos = new int[TOTAL_CLIENTES];

        // Llamo a la función para limpiar la matriz y dejarla en cero antes de empezar
        inicializarMatriz(matrizFinanciera, TOTAL_CLIENTES, COLUMNAS_FINANZAS);

        System.out.println("=====================================================");
        System.out.println("   SISTEMA DE AUDITORIA FINANCIERA - MATRICIAL V2    ");
        System.out.println("=====================================================");

        for (int i = 0; i < TOTAL_CLIENTES; i++) {
            System.out.println("\n=================================================================");
            System.out.println(">>> AUDITORIA CENTRAL: PROCESANDO REGISTRO DE CONTROL #" + (i + 1) + " <<<");
            System.out.println("=================================================================");
            
            // Guardo el nombre en la posición i del arreglo
            System.out.print("Nombre completo del cliente: ");
            nombresClientes[i] = input.nextLine().toUpperCase();
            
            // Reviso si el cliente es VIP para saber si le toca descuento
            tiposClientes[i] = capturarTipoCliente(input);
            
            System.out.print("Marca del vehiculo: ");
            marcasVehiculos[i] = input.nextLine().toUpperCase();
            
            System.out.print("Modelo del vehiculo: ");
            modelosVehiculos[i] = input.nextLine().toUpperCase();
            
            aniosVehiculos[i] = capturarAnioVehiculo(input);
            
            System.out.print("Numero de placa: ");
            placasVehiculos[i] = input.nextLine().toUpperCase();

            int departamento = capturarDepartamento(input);
            
            // En la columna 0 guardo si es Mecánica o Electricidad
            matrizFinanciera[i][0] = departamento;
            
            int servicioCodigo = 0;
            double precioBase = 0.0;
            String txtServicio = "";

            if (departamento == 1) {
                servicioCodigo = desplegarMenuMecanica(input);
                precioBase = evaluarPrecioMecanica(servicioCodigo);
                txtServicio = evaluarNombreMecanica(servicioCodigo);
                
            } if (departamento ==2){
                servicioCodigo = desplegarMenuElectricidad(input);
                precioBase = evaluarPrecioElectricidad(servicioCodigo);
                txtServicio = evaluarNombreElectricidad(servicioCodigo);
                
            } else{
                servicioCodigo = desplegarMenuPintura(input);
                precioBase = evaluarPrecioPintura(servicioCodigo);
                txtServicio = evaluarNombrePintura(servicioCodigo);
            }
            
            // Guardo el código del servicio en la columna 1 y el precio base en la columna 2
            matrizFinanciera[i][1] = servicioCodigo;
            matrizFinanciera[i][2] = precioBase;
            nombresServicios[i] = txtServicio;

            int diasTaller = capturarDiasTaller(input);
            
            // En la columna 3 guardo los días y en la columna 4 calculo el costo de estadía (días * L.50)
            matrizFinanciera[i][3] = diasTaller;
            double costoEstadia = calcularCostoEstadia(diasTaller, 50.0);
            matrizFinanciera[i][4] = costoEstadia;

            // En la columna 5 sumo el precio del servicio más los días en el taller
            double subtotalNeto = calcularSubtotalNeto(precioBase, costoEstadia);
            matrizFinanciera[i][5] = subtotalNeto;

            // Calculo el descuento (columna 6 guarda la tasa y la columna 7 guarda los lempiras ganados por descuento)
            double tasaDescuento = evaluarTasaDescuento(tiposClientes[i]);
            double montoDescuento = calcularMontoDescuento(subtotalNeto, tasaDescuento);
            matrizFinanciera[i][6] = tasaDescuento;
            matrizFinanciera[i][7] = montoDescuento;

            // Registro cómo va a pagar y calculo la comisión en la columna 8 si usa tarjeta
            String metodo = capturarMetodoPago(input);
            metodosPago[i] = metodo;
            double cargoComision = calcularCargoTarjeta(subtotalNeto, metodo);
            matrizFinanciera[i][8] = cargoComision;

            // Resto el descuento, sumo el recargo de tarjeta y calculo el 15% de ISV en la columna 9
            double subtotalConAjustes = (subtotalNeto - montoDescuento) + cargoComision;
            double impuestoISV = calcularImpuestoISV(subtotalConAjustes);
            matrizFinanciera[i][9] = impuestoISV;

            // Saco el total final que el cliente debe pagar y lo pongo en la columna 10
            double granTotal = calcularGranTotal(subtotalConAjustes, impuestoISV);
            matrizFinanciera[i][10] = granTotal;

            imprimirFacturaIndividual(
                nombresClientes[i], tiposClientes[i], marcasVehiculos[i], modelosVehiculos[i], aniosVehiculos[i], placasVehiculos[i],
                txtServicio, precioBase, diasTaller, costoEstadia, subtotalNeto, montoDescuento, cargoComision, impuestoISV, metodo, granTotal
            );
            
            input.nextLine();
        } 

        // Al final de los 3 clientes, llamo a la función para que me tire el reporte general de la caja
        desplegarBalanceConsolidado(nombresClientes, tiposClientes, nombresServicios, placasVehiculos, metodosPago, matrizFinanciera, TOTAL_CLIENTES);
    }

    // Esta función limpia toda la matriz usando ciclos for anidados
    public static void inicializarMatriz(double[][] m, int f, int c) {
        for (int i = 0; i < f; i++) {
            for (int j = 0; j < c; j++) {
                m[i][j] = 0.0;
            }
        }
    }

    // Valido el tipo de cliente usando un ciclo do-while para que elija bien
    public static String capturarTipoCliente(Scanner scan) {
        int sel;
        do {
            System.out.println("Clasificacion de Antiguedad:");
            System.out.println("  1. Cliente Regular (Sin beneficios especiales)");
            System.out.println("  2. Cliente VIP Frecuente (Aplica 10% de descuento)");
            System.out.print("Seleccione la clasificacion (1-2): ");
            sel = scan.nextInt();
        } while (sel < 1 || sel > 2);
        scan.nextLine();
        
        if (sel == 2) {
            return "VIP";
        } else {
            return "REGULAR";
        }
    }

    //Captura datos del año del vehiculo que ingresamos
    public static int capturarAnioVehiculo(Scanner scan) {
        int anio;
        System.out.print("Ingrese el año de fabricacion del vehiculo: ");
        anio = scan.nextInt();
        while (anio < 1950 || anio > 2027) {
            System.out.println(">> ERROR: Año fuera de rango.");
            System.out.print("Ingrese un año valido (1950-2027): ");
            anio = scan.nextInt();
        }
        scan.nextLine();
        return anio;
    }

    //Pedimos hacia que departamento viene el vehiculo 
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

    //Menu de mecanica general
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
        } else if (op == 2) {
            return "Direccion Hidraulica";
        } else if (op == 3) {
            return "Sistema de Frenos";
        } else if (op == 4) {
            return "Suspension Completa";
        }
        return "Desconocido";
    }

    //Menu del departamento de electricidad
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
    
    //Menu del departamento de pintura
    public static int desplegarMenuPintura(Scanner scan) {
        int op;
        do {
            System.out.println("\nMENU DE SERVICIOS: DEPARTAMENTO DE PINTURA");
            System.out.println("1. Pintura en general ...... L. 15,000.00");
            System.out.println("2. Relleno o golpes ....... L. 4,500.00");
            System.out.println("3. Robin o pulido de pintura ...... L. 3,500.00");
            System.out.print("Seleccione el servicio de pintura que va a realizar (1-3): ");
            op = scan.nextInt();
        } while (op < 1 || op > 4);
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
        } else if (op == 2) {
            return "Relleno o golpes";
        } else if (op == 3) {
            return "Pulido de pintura";
        }
        return "Desconocido";
    }


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

    // Registro el método de pago elegido y valido que sea de las opciones dadas
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
        } else if (formaPago == 3) {
            return "TRANSFERENCIA";
        }
        return "EFECTIVO";
    }

    public static double calcularCostoEstadia(int d, double p) { 
        return d * p; 
    }
    
    public static double calcularSubtotalNeto(double s, double e) { 
        return s + e; 
    }
    
    // Funciones pequeñas para calcular los descuentos, recargos bancarios e ISV de forma separada
    public static double evaluarTasaDescuento(String t) { 
        if (t.equals("VIP")) {
            return 0.10;
        }
        return 0.0;
    }
    
    public static double calcularMontoDescuento(double s, double t) { 
        return s * t; 
    }
    
    public static double calcularCargoTarjeta(double s, String m) { 
        if (m.equals("TARJETA")) {
            return s * 0.05;
        }
        return 0.0;
    }
    
    public static double calcularImpuestoISV(double s) { 
        return s * 0.15; 
    }
    
    public static double calcularGranTotal(double s, double i) { 
        return s + i; 
    }

    public static void imprimirFacturaIndividual(String c, String t, String ma, String mo, int an, String pl, String se, double pb, int di, double cd, double sub, double de, double co, double isv, String pa, double tot) {
        System.out.println("\n-----------------------------------------------------------------");
        System.out.println("                   COMPROBANTE DE COMPRA FISCAL                  ");
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

    // Esta función recorre la matriz con un ciclo for para ir sumando todo el dinero acumulado en el día
    public static void desplegarBalanceConsolidado(String[] clie, String[] tipo, String[] serv, String[] plac, String[] pago, double[][] finanzas, int filas) {
        System.out.println("\n========================================================================================================================================");
        System.out.println("                               REPORTE GERENCIAL CONSOLIDADO - BALANCE DE CAJA CENTRAL                              ");
        System.out.println("========================================================================================================================================");
        
        // Cabecera con los títulos bien alineados
        System.out.printf("%-15s %-12s %-22s %-12s %-15s %-12s %-12s %-12s %-12s\n", 
                "Cliente", "Categoria", "Servicio Realizado", "Placa", "Metodo Pago", "Costo Base", "Descuento", "ISV (15%)", "Total General");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        
        double acumuladorPrecioBase = 0.0;
        double acumuladorDescuentos = 0.0;
        double acumuladorISV = 0.0;
        double acumuladorTotales = 0.0;
        
        // El ciclo recorre las filas para imprimir y sumar los datos de la matriz
        for (int i = 0; i < filas; i++) {
             
            System.out.printf("%-15s %-12s %-22s %-12s %-15s L.%,-10.1f L.%,-10.1f L.%,-10.1f L.%,-10.1f\n", 
                    clie[i], tipo[i], serv[i], plac[i], pago[i], finanzas[i][2], finanzas[i][7], finanzas[i][9], finanzas[i][10]
            );
            
            acumuladorPrecioBase += finanzas[i][2];
            acumuladorDescuentos += finanzas[i][7];
            acumuladorISV        += finanzas[i][9];
            acumuladorTotales    += finanzas[i][10];
        }
        
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" METRICAS GLOBALES:  Ingresos Base: L.%,.2f  |  Descuentos Cedidos: L.%,.2f  |  Impuestos Recaudados: L.%,.2f\n", 
                acumuladorPrecioBase, acumuladorDescuentos, acumuladorISV);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" INGRESO NETO TOTAL EN CAJA : L. %,.2f\n", acumuladorTotales);
        System.out.println("========================================================================================================================================\n");
    }//fin de BalanceoConsolidado
    
}//fin de class
