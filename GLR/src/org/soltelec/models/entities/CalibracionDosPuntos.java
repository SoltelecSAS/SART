/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.models.entities;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Dany
 */
public class CalibracionDosPuntos extends Calibracion{
    
    public static final String TABLA_DOS_PUNTOS = "calibracion_dos_puntos";
    public static final String BM_CO = "bm_co";
    public static final String BM_CO2 = "bm_co2";
    public static final String BM_HC = "bm_hc";
    public static final String ALTA_CO = "alta_co";
    public static final String ALTA_CO2 = "alta_co2";
    public static final String ALTA_HC = "alta_hc";    
    public static final String BANCO_BM_CO = "banco_bm_co";
    public static final String BANCO_BM_CO2 = "banco_bm_co2";
    public static final String BANCO_BM_O2 = "banco_bm_o2";
    public static final String BANCO_BM_HC = "banco_bm_hc";
    public static final String BANCO_ALTA_CO = "banco_alta_co";
    public static final String BANCO_ALTA_CO2 = "banco_alta_co2";
    public static final String BANCO_ALTA_O2 = "banco_alta_o2";
    public static final String BANCO_ALTA_HC = "banco_alta_hc";
    public static final String LAB_PIPETA_ALTA = "laboratorio_pipeta_alta";
    public static final String CIL_PIPETA_ALTA = "cilindro_pipeta_alta";
    public static final String CER_PIPETA_ALTA = "certificado_pipeta_alta";
    public static final String LAB_PIPETA_BAJA = "laboratorio_pipeta_baja";
    public static final String CIL_PIPETA_BAJA = "cilindro_pipeta_baja";
    public static final String CER_PIPETA_BAJA = "certificado_pipeta_baja";
    
    private double bmCo;
    private double bmCo2;
    private double bmHc;
    private double altaCo;
    private double altaCo2;
    private double altaHc;
    private double bancoBmCo;
    private double bancoBmCo2;
    private double bancoBmo2;
    private double bancoBmHc;
    private double bancoAltaCo;
    private double bancoAltaCo2;
    private double bancoAltao2;
    private double bancoAltaHc;
    private String labPipetaAlta;
    private String cilPipetaAlta;
    private String cerPipetasAlta;
    private String labPipetaBaja;
    private String cilPipetaBaja;
    private String cerPipetaBaja;

    public CalibracionDosPuntos() {
        setIdTipoCalibracion(2);
    }
    
    public double getBmCo() {
        return bmCo;
    }

    public void setBmCo(double bmCo) {
        this.bmCo = bmCo;
    }

    public double getBmCo2() {
        return bmCo2;
    }

    public void setBmCo2(double bmCo2) {
        this.bmCo2 = bmCo2;
    }

    public double getBmHc() {
        return bmHc;
    }

    public void setBmHc(double bmHc) {
        this.bmHc = bmHc;
    }

    public double getAltaCo() {
        return altaCo;
    }

    public void setAltaCo(double altaCo) {
        this.altaCo = altaCo;
    }

    public double getAltaCo2() {
        return altaCo2;
    }

    public void setAltaCo2(double altaCo2) {
        this.altaCo2 = altaCo2;
    }

    public double getAltaHc() {
        return altaHc;
    }

    public void setAltaHc(double altaHc) {
        this.altaHc = altaHc;
    }

    public double getBancoBmCo() {
        return bancoBmCo;
    }

    public void setBancoBmCo(double bancoBmCo) {
        this.bancoBmCo = bancoBmCo;
    }

    public double getBancoBmCo2() {
        return bancoBmCo2;
    }

    public void setBancoBmCo2(double bancoBmCo2) {
        this.bancoBmCo2 = bancoBmCo2;
    }

    public double getBancoBmHc() {
        return bancoBmHc;
    }

    public void setBancoBmHc(double bancoBmHc) {
        this.bancoBmHc = bancoBmHc;
    }

    public double getBancoAltaCo() {
        return bancoAltaCo;
    }

    public void setBancoAltaCo(double bancoAltaCo) {
        this.bancoAltaCo = bancoAltaCo;
    }

    public double getBancoAltaCo2() {
        return bancoAltaCo2;
    }

    public void setBancoAltaCo2(double bancoAltaCo2) {
        this.bancoAltaCo2 = bancoAltaCo2;
    }

    public double getBancoAltaHc() {
        return bancoAltaHc;
    }

    public void setBancoAltaHc(double bancoAltaHc) {
        this.bancoAltaHc = bancoAltaHc;
    }

    public double getBancoBmo2() {
        return bancoBmo2;
    }

    public void setBancoBmo2(double bancoBmo2) {
        this.bancoBmo2 = bancoBmo2;
    }

    public double getBancoAltao2() {
        return bancoAltao2;
    }

    public void setBancoAltao2(double bancoAltao2) {
        this.bancoAltao2 = bancoAltao2;
    }
    
    
    
    public List<String> getCamposTabla() {
        return Arrays.asList(Calibracion.ID_CALIBRACION,BM_CO, BM_CO2, BM_HC, ALTA_CO, ALTA_CO2, ALTA_HC,
                            BANCO_BM_CO, BANCO_BM_CO2,BANCO_BM_O2, BANCO_BM_HC, BANCO_ALTA_CO, BANCO_ALTA_CO2,BANCO_ALTA_O2,
                            BANCO_ALTA_HC,LAB_PIPETA_ALTA,CIL_PIPETA_ALTA,CER_PIPETA_ALTA,LAB_PIPETA_BAJA,CIL_PIPETA_BAJA,CER_PIPETA_BAJA);
    }

    public List getValores() {
        return Arrays.asList(getIdCalibracion(), bmCo, bmCo2, bmHc, altaCo, altaCo2, altaHc,
                            bancoBmCo, bancoBmCo2,bancoBmo2, bancoBmHc, bancoAltaCo, bancoAltaCo2,bancoAltao2, bancoAltaHc,labPipetaAlta,
                            cilPipetaAlta,cerPipetasAlta,labPipetaBaja,cilPipetaBaja,cerPipetaBaja);
    }  

    public String getLabPipetaAlta() {
        return labPipetaAlta;
    }

    public void setLabPipetaAlta(String labPipetaAlta) {
        this.labPipetaAlta = labPipetaAlta;
    }

    public String getCilPipetaAlta() {
        return cilPipetaAlta;
    }

    public void setCilPipetaAlta(String cilPipetaAlta) {
        this.cilPipetaAlta = cilPipetaAlta;
    }

    public String getCerPipetasAlta() {
        return cerPipetasAlta;
    }

    public void setCerPipetasAlta(String cerPipetasAlta) {
        this.cerPipetasAlta = cerPipetasAlta;
    }

    public String getLabPipetaBaja() {
        return labPipetaBaja;
    }

    public void setLabPipetaBaja(String labPipetaBaja) {
        this.labPipetaBaja = labPipetaBaja;
    }

    public String getCilPipetaBaja() {
        return cilPipetaBaja;
    }

    public void setCilPipetaBaja(String cilPipetaBaja) {
        this.cilPipetaBaja = cilPipetaBaja;
    }

    public String getCerPipetaBaja() {
        return cerPipetaBaja;
    }

    public void setCerPipetaBaja(String cerPipetaBaja) {
        this.cerPipetaBaja = cerPipetaBaja;
    }

  
    
    
    
    
    
}
