package com.soltelec.opacimetro.brianbeeold;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class MensajeOpacimetroBrianBee
/*    */ {
/*    */   private String comando;
/*    */   private List<String> datos;
/*    */   
/*    */   public MensajeOpacimetroBrianBee(String comando)
/*    */   {
/* 12 */     this.comando = comando;
/* 13 */     this.datos = new java.util.ArrayList();
/*    */   }
/*    */   
/*    */   public String getComando() {
/* 17 */     return this.comando;
/*    */   }
/*    */   
/*    */   public void setComando(String comando) {
/* 21 */     this.comando = comando;
/*    */   }
/*    */   
/*    */   public List<String> getDatos() {
/* 25 */     return this.datos;
/*    */   }
/*    */   
/*    */   public void setDatos(List<String> datos) {
/* 29 */     this.datos = datos;
/*    */   }
/*    */ }


/* Location:              /home/luisberna/Dropbox/maven-soltelec/Sart-git-3/modulos/ModuloGLR.jar!/com/soltelec/opacimetro/brianbee/MensajeOpacimetroBrianBee.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */