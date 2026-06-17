/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectofacturacion;

import java.util.Scanner;



/**
 *
 * @author josue
 */
public class ProyectoFacturacion {

    /**
     * Facturacion de taller
     * Aquí queremos analizar cuanto será el gasto de cada cliente por reparar su vehículo, ya sea entonces por cambio de aceite, reemplazo de dirección asistida, reparación de A/C y pintura.
            Entonces aquí registraremos el vehículo de cada persona, los días que tomara cada reparación y el costo total del vehículo 
            Entonces aquí queremos lograr:
           - Registro de clientes y vehículo 
           - Tipo de reparación que viene a hacer 
           - Metodo de pago 
           - Facturación

     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        //variable de numeros de cliente
        int numeroCliente = 0;
        
        //Menu principal 
        //Ciclo while para repetir el programa
        //osea la cantidad de clientes que ingresemos este va a repetir lo mismo
        System.out.println("Bienvenidos al sistema de facturacion");
        System.out.println("Del taller automotriz nuevos horizontes");
        
        System.out.print("Cuantos clientes desea ingresar: ");
        numeroCliente = input.nextInt();
        input.nextLine();
        
        
        while(numeroCliente <=0 ){
            System.out.println("El numero tiene que ser mayor a 0");
            System.out.print("Ingrese nuevamente cuantos clientes va a registrar: ");
            numeroCliente = input.nextInt();
            input.nextLine();
        }//fin de while
        
        //empezando el ciclo for para que acumule clientes y no pueda ser 0
        for (int cliente = 1; cliente <= numeroCliente; cliente++){
            System.out.println("Cliente #" + cliente);
            
            //pide ingresar los datos del cliente
            String nombreCliente;
            System.out.println("Ingrese el nombre del cliente");
            nombreCliente = input.nextLine();
            
            String marcaVehiculo; 
            System.out.println("Ingrese la marca del vehiculo");
            marcaVehiculo = input.nextLine();
            
            String modeloVehiculo; 
            System.out.println("Ingrese el modelo del vehiculo:");
            modeloVehiculo = input.nextLine();
            
            int anioVehiculo;
            System.out.println("Ingrese el año del vehiculo");
            anioVehiculo = input.nextInt();
            
            String placaVehiculo; 
            System.out.println("Ingrese la placa del vehiculo:");
            placaVehiculo = input.nextLine();
            
            //opciones para la reparacion del vehiculo 
            int opcionReparacion = getOpcionReparacion(input);

            //Obtener el nombre de la reparacion que se hara 
            String reparacion = getNombreReparacion(opcionReparacion);
            
            //obtener el precio por dicha reparacion 
            double precioReparacion = obtenerPrecioReparacion(opcionReparacion);

            //pidiendo cuantos dias de trabajo se tomaran 
            int diasTrabajo = pedirDiasTrabajo(input);

            //costo fijo por el dia de trabajo 
            double costoPorDia = 50;
            
            //costo por dia de trabajo + dias de trabajo que se tomaran  
            double costoDias = calcularCostoDias(diasTrabajo, costoPorDia);
            
            //calcular el subtotal de el precio de la reparacion + los dias que se trabajaron
            double subtotal = calcularSubtotal(precioReparacion, costoDias);
            
            //impuestos del 15% 
            double impuesto = calcularImpuesto(subtotal);
            
            //el precio total a pagar de todo su servicio 
            double totalPagar = calcularTotal(subtotal, impuesto);
            
            /*metodo de pago donde pediremos si es 
            1. Efectivo
            2. Tarjeta
            3. Transferencia
            */
            String metodoPago = obtenerMetodoPago (input);

            //datos que se mostraran en la factura final 
            mostrarFactura(
                    nombreCliente,
                    marcaVehiculo,
                    modeloVehiculo,
                    anioVehiculo,
                    placaVehiculo,
                    reparacion,
                    precioReparacion,
                    diasTrabajo,
                    costoDias,
                    subtotal,
                    impuesto,
                    metodoPago,
                    totalPagar
            );

            input.nextLine();
        }//fin de for

        System.out.println("\nGracias por usar el sistema de facturacion.");
    }//fin del main principal 

    //datos para imprimir el texto y pedir al usuario que los ingrese
    public static String impTexto(Scanner input, String mensaje) {
        String texto;

        System.out.print(mensaje);
        texto = input.nextLine();

        return texto;
    }//fin de impresion del texto 

    //lista de las reparaciones que se hacen en el taller 
    public static int getOpcionReparacion(Scanner input) {
        int opcion;

        do {
            System.out.println("\nTIPO DE REPARACION");
            System.out.println("1. Cambio de aceite");
            System.out.println("2. Reemplazo de direccion asistida");
            System.out.println("3. Reparacion de A/C");
            System.out.println("4. Pintura");
            System.out.print("Seleccione una opcion: ");
            opcion = input.nextInt();

            if (opcion < 1 || opcion > 4) {
                System.out.println("Opcion incorrecta. Intente nuevamente.");
            }

        } while (opcion < 1 || opcion > 4);

        return opcion;
    }//fin de opcion reparacion

    //seleccion de numero en la lista de reparaciones 
    public static String getNombreReparacion(int opcion) {
        String reparacion = "";

        if (opcion == 1) {
            reparacion = "Cambio de aceite";
        } else if (opcion == 2) {
            reparacion = "Reemplazo de direccion asistida";
        } else if (opcion == 3) {
            reparacion = "Reparacion de A/C";
        } else if (opcion == 4) {
            reparacion = "Pintura";
        }

        return reparacion;
    }//fin de nombre de reparacion 

    //Precio de cada reparacion 
    public static double obtenerPrecioReparacion(int opcion) {
        double precio = 0;

        //cambio de aceite
        if (opcion == 1) {
            precio = 800;
            
        //Direccion Asistida
        } else if (opcion == 2) {
            precio = 3500;
            
        //A/C
        } else if (opcion == 3) {
            precio = 4500;
            
        //Pintura
        } else if (opcion == 4) {
            precio = 6000;
        }

        return precio;
    }//fin de precios de reparacion 

    //dias que tomara la reparacion
    public static int pedirDiasTrabajo(Scanner input) {
        int dias;

        System.out.print("Ingrese los dias que tomara la reparacion: ");
        dias = input.nextInt();

        while (dias <= 0) {
            System.out.println("Los dias deben ser mayores que cero.");
            System.out.print("Ingrese nuevamente los dias: ");
            dias = input.nextInt();
        }

        return dias;
    }//fin de dias de trabajo 

    //calcular el costo total de dias ingresados
    public static double calcularCostoDias(int dias, double costoPorDia) {
        double costoDias;

        costoDias = dias * costoPorDia;

        return costoDias;
    }// fin de costo de dias 

    //calculo del subtotal antes de impuestos
    public static double calcularSubtotal(double precioReparacion, double costoDias) {
        double subtotal;

        subtotal = precioReparacion + costoDias;

        return subtotal;
    }//fin del subtotal 

    //calculo del subtotal + impuestos 
    public static double calcularImpuesto(double subtotal) {
        double impuesto;

        impuesto = subtotal * 0.15;

        return impuesto;
    }//fin de impuestos 

    //calculo total de la factura 
    public static double calcularTotal(double subtotal, double impuesto) {
        double total;

        total = subtotal + impuesto;

        return total;
    }//fin de total 
    
    public static String obtenerMetodoPago(Scanner input) {
    int opcion;
    String metodo = "";

    do {
        System.out.println("\nMETODO DE PAGO");
        System.out.println("1. Efectivo");
        System.out.println("2. Tarjeta");
        System.out.println("3. Transferencia");
        System.out.print("Seleccione una opcion: ");
        opcion = input.nextInt();

        if (opcion < 1 || opcion > 3) {
            System.out.println("Opcion incorrecta.");
        }

    } while (opcion < 1 || opcion > 3);

    if (opcion == 1) {
        metodo = "Efectivo";
    } else if (opcion == 2) {
        metodo = "Tarjeta";
    } else if (opcion == 3) {
        metodo = "Transferencia";
    }

    return metodo;
}

    //datos de la factura al final del programa de cada cliente
    public static void mostrarFactura(
            String nombreCliente, 
            String marcaVehiculo,
            String modeloVehiculo,
            int anioVehiculo,
            String placaVehiculo,
            String reparacion,
            double precioReparacion,
            int diasTrabajo,
            double costoDias,
            double subtotal,
            double impuesto,
            String metodoPago,
            double totalPagar) {

        System.out.println("\n==================================");
        System.out.println(" RESUMEN DE FACTURA");
        System.out.println("==================================");
        System.out.println("Cliente: " + nombreCliente);
        System.out.println("Vehiculo: " + marcaVehiculo + " " + modeloVehiculo);
        System.out.println("Anio del vehiculo: "+anioVehiculo);
        System.out.println("Placa: " + placaVehiculo);
        System.out.println("Reparacion: " + reparacion);
        System.out.println("Precio reparacion: L. " + precioReparacion);
        System.out.println("Dias de trabajo: " + diasTrabajo);
        System.out.println("Costo por dias: L. " + costoDias);
        System.out.println("Subtotal: L. " + subtotal);
        System.out.println("Impuesto 15%: L. " + impuesto);
        System.out.println("Metodo de pago: " + metodoPago);
        System.out.println("===================================");
        System.out.println("Total a pagar: L. " + totalPagar);
        System.out.println("==================================");
        
        
    }//fin de mostrar factura 
    
}//fin de class