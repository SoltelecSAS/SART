package com.soltelec.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.soltelec.conexion_seriales.Conexion;

public class AddTablas {

    public static void actualizarTablasDb() {
        Conexion.setConexionFromFile();

        // 🔹 Concatenamos todos los bloques de todos los script que crear tablas y datos a esas tablas
        String script =
                getScriptAgregarColumnasCda() +
                getScriptCrearCausalesAbortos() +
                getScriptAgregarFkAbortos() +
                getScriptAgregarColumnaEmpresaPropietarios() +
                getScriptTablasRechazoGases() +
                getScriptInsertAbortos() +
                getScriptInsertRechazoGases();

        try (Connection conn = DriverManager.getConnection(
                Conexion.getUrl(),
                Conexion.getUsuario(),
                Conexion.getContrasena());
            Statement stmt = conn.createStatement()) {

            // 🔹 Ejecutamos cada sentencia separada por ';'
            for (String sql : script.split(";")) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    if (!sql.endsWith(";")) {
                        sql = sql + ";";
                    }
                    try {
                        stmt.execute(sql);
                    } catch (Exception ex) {
                        System.err.println("Error ejecutando: " + sql);
                        ex.printStackTrace();
                    }
                }
            }

            System.out.println("Script ejecutado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getScriptAgregarColumnasCda() {
        return
            "-- Agregar columnas a tabla cda si no existen\n" +
            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='cda' AND COLUMN_NAME='id_cda_bogota' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE cda ADD COLUMN id_cda_bogota INT NULL','SELECT \"Column id_cda_bogota already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n" +

            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='cda' AND COLUMN_NAME='grant_type_bogota' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE cda ADD COLUMN grant_type_bogota VARCHAR(50) NULL','SELECT \"Column grant_type_bogota already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n" +

            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='cda' AND COLUMN_NAME='client_id_bogota' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE cda ADD COLUMN client_id_bogota BIGINT NULL','SELECT \"Column client_id_bogota already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n" +

            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='cda' AND COLUMN_NAME='client_secret_bogota' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE cda ADD COLUMN client_secret_bogota VARCHAR(255) NULL','SELECT \"Column client_secret_bogota already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n" +

            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='cda' AND COLUMN_NAME='codigo_ciudad' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE cda ADD COLUMN codigo_ciudad VARCHAR(255) NULL','SELECT \"Column codigo_ciudad already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n";
    }

    private static String getScriptCrearCausalesAbortos() {
        return
            "CREATE TABLE IF NOT EXISTS causales_abortos (\n" +
            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    descripcion VARCHAR(255) NOT NULL,\n" +
            "    codigo VARCHAR(50) NOT NULL\n" +
            ");\n";
    }

    private static String getScriptAgregarFkAbortos() {
        return
            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='pruebas' AND COLUMN_NAME='id_aborto' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE pruebas ADD COLUMN id_aborto INT NULL','SELECT \"Column id_aborto in pruebas already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n" +

            "SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_NAME='fk_pruebas_causales_abortos' AND TABLE_NAME='pruebas' AND CONSTRAINT_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@fk_exists=0,'ALTER TABLE pruebas ADD CONSTRAINT fk_pruebas_causales_abortos FOREIGN KEY (id_aborto) REFERENCES causales_abortos(id) ON DELETE SET NULL','SELECT \"Constraint fk_pruebas_causales_abortos already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n";
    }

    private static String getScriptAgregarColumnaEmpresaPropietarios() {
        return
            "SET @col_exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='propietarios' AND COLUMN_NAME='empresa' AND TABLE_SCHEMA=DATABASE());\n" +
            "SET @sql := IF(@col_exists=0,'ALTER TABLE propietarios ADD COLUMN empresa VARCHAR(100) NULL','SELECT \"Column empresa in propietarios already exists\"');\n" +
            "PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;\n";
    }

    private static String getScriptTablasRechazoGases() {
        return
            "CREATE TABLE IF NOT EXISTS rechazo_gases (\n" +
            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    codigo INT NOT NULL,\n" +
            "    causa_general VARCHAR(255) NULL,\n" +
            "    nombre_campo VARCHAR(255) NULL,\n" +
            "    descripcion VARCHAR(500) NULL,\n" +
            "    CONSTRAINT uq_rechazo_gases_codigo UNIQUE (codigo)\n" +
            ");\n" +

            "CREATE TABLE IF NOT EXISTS pruebas_rechazo_gases (\n" +
            "    id_prueba INT NOT NULL,\n" +
            "    id_rechazo_gases INT NOT NULL,\n" +
            "    PRIMARY KEY (id_prueba, id_rechazo_gases),\n" +
            "    CONSTRAINT fk_pruebas_rechazo_gases_prueba FOREIGN KEY (id_prueba) REFERENCES pruebas(Id_Pruebas) ON DELETE CASCADE,\n" +
            "    CONSTRAINT fk_pruebas_rechazo_gases_causal FOREIGN KEY (id_rechazo_gases) REFERENCES rechazo_gases(id) ON DELETE CASCADE\n" +
            ");\n";
    }

    private static String getScriptInsertAbortos() {
        return
            "INSERT IGNORE INTO causales_abortos (id, codigo, descripcion)\n" +
            "VALUES \n" +
            "(1, '1', 'FALLAS DEL EQUIPO DE MEDICION'),\n" +
            "(2, '2', 'FALLA SUBITA DEL FLUIDO ELECTRICO'),\n" +
            "(3, '3', 'BLOQUEO FORZADO DEL EQUIPO'),\n" +
            "(4, '4', 'EJECUCION INCORRECTA DE LA PRUEBA'),\n" +
            "(5, '5', 'FALLA POR DESVIACION DEL CERO');\n";
    }

    private static String getScriptInsertRechazoGases() {
        return
            "INSERT IGNORE INTO rechazo_gases (id, codigo, causa_general, nombre_campo, descripcion)\n" +
            "VALUES\n" +
            "(1, 0, 'SIN RECHAZO', 'NO APLICA', 'Al aprobar la prueba total no se genera rechazo'),\n" +
            "(2, 1, 'RECHAZO, INCUMPLIMIENTO VEHICULOS TIPO OTTO - DIESEL - MOTO', 'RTM', 'Vigencia de RTM'),\n" +
            "(3, 2, 'RECHAZO, INCUMPLIMIENTO VEHICULOS TIPO OTTO - DIESEL - MOTO', 'Generadores ruido', 'De acuerdo con el DECRETO 1076 2015 ART. 2.2.5.1.5.20'),\n" +
            "(4, 3, 'RECHAZO, INCUMPLIMIENTO VEHICULOS TIPO DIESEL', 'PICO Y PLACA AMBIENTAL', 'DE ACUERDO CON EL DECRETO 174 2006'),\n" +
            "(5, 4, 'RECHAZO, INCUMPLIMIENTO VEHICULOS TIPO OTTO - DIESEL - MOTO', 'Restriccion vehicular', 'DE ACUERDO DECRETO 840 2019/ DECRETO 077 MARZO 2020'),\n" +
            "(6, 5, 'RECHAZO, INCUMPLIMIENTO VEHICULOS TIPO OTTO - DIESEL - MOTO', 'Ausencia silenciador', 'DE ACUERDO A LA LEY 1383 2010 ART. 122 INFRACCION C28'),\n" +
            "(7, 6, 'RECHAZO, INCUMPLIMIENTO VEHICULOS TIPO DIESEL', 'Incumplimiento decreto 1552', 'Cumplimiento de la medida enmarcada en el DECRETO 1552 DE 2000'),\n" +
            "(8, 7, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Fugas tubo escape', 'Existencia de fugas en el tubo y uniones del multiple del sistema de escape'),\n" +
            "(9, 8, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Fugas silenciador', 'Existencia de fugas en el silenciador del sistema de escape'),\n" +
            "(10, 9, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Accesorios deformaciones obstrucciones tubo escape', 'Instalacion de accesorios o deformaciones en el tubo de escape que no permitan la introduccion del acople'),\n" +
            "(11, 10, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Ausencia o fugas tapa combustible', 'Ausencia de tapa de llenado de combustible o fugas en el mismo'),\n" +
            "(12, 11, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Ausencia o mal estado filtro aire', 'Ausencia o incorrecta instalacion del filtro de aire'),\n" +
            "(13, 12, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Ausencia o fugas tapa aceite', 'Ausencia de tapones de aceite o fugas en el mismo'),\n" +
            "(14, 13, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Salidas adicionales al diseno sistema escape', 'Salidas adicionales en el sistema de escape diferentes al diseno original del vehiculo'),\n" +
            "(15, 14, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Tiene sistema PCV', 'Desconexion de sistemas de recirculacion de gases provenientes del carter del motor'),\n" +
            "(16, 15, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Presencia humo negro azul', 'Presencia de humo azul o negro'),\n" +
            "(17, 16, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Dispositivos que alteren RPM', 'RPM inestables o fuera de rango'),\n" +
            "(18, 17, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'RPM fuera de rango', 'RPM inestables o fuera de rango'),\n" +
            "(19, 18, 'FALLA EN INSPECCION VISUAL PRELIMINAR', 'Falla sistema refrigeracion', 'Incorrecta operacion del sistema de refrigeracion'),\n" +
            "(20, 19, 'CONDICIONES INSEGURAS DE OPERACION (VISIBLE O SONORA)', 'Indicacion de mal funcionamiento del motor', 'Falla por temperatura del motor'),\n" +
            "(21, 20, 'EL GOBERNADOR DE LA BOMBA DE INYECCION NO LIMITA LA VELOCIDAD DEL MOTOR', 'Funcionamiento del sistema control de velocidad del motor', 'El gobernador de la bomba de inyeccion no limita la velocidad del motor.'),\n" +
            "(22, 21, 'RPM INESTABLES DURANTE CICLOS DE MEDICION', 'Inestabilidad durante ciclos de medicion', 'Inestabilidad durante ciclos de medicion'),\n" +
            "(23, 22, 'DIFERENCIAS ARITMETICAS DE OPACIDAD > 5%', 'Diferencias aritmeticas', 'Diferencia aritmetica'),\n" +
            "(24, 23, 'RPM GOBERNADAS NO ALCANZADAS EN 5 SEG', 'Velocidad alcanzada en 5 segundos', 'Velocidad alcanzada en 5 segundos'),\n" +
            "(25, 24, 'SISTEMA DE CONTROL DE GIRO DEL MOTOR (GOBERNADOR)', 'RPM Velocidad gobernada', 'RPM Velocidad gobernada'),\n" +
            "(26, 25, 'DEFICIENTES CONDICIONES DE OPERACION (FALLA SUBITA DE MOTOR O VEHICULO)', 'Falla subita de motor', 'Falla subita de motor'),\n" +
            "(27, 26, 'DILUCION, CO2 SUPERIOR A 5%', 'Presencia de dilucion', 'Presencia de dilucion'),\n" +
            "(28, 27, 'DILUCION', 'DILUCION', 'DILUCION'),\n" +
            "(29, 28, 'RECHAZO, INCUMPLIMIENTO DE NIVELES MAXIMOS PERMITIDOS POR LA AUTORIDAD COMPETENTE', 'HC en crucero', 'HC en crucero');\n";
    }
}
