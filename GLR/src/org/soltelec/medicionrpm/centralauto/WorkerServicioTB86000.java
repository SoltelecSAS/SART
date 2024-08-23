package org.soltelec.medicionrpm.centralauto;

import javax.swing.SwingWorker;






class WorkerServicioTB86000 extends SwingWorker<Void,Void>{

        private PanelServicioTB8600 panel;
        private TB86000 tb86000;
        int rpm ,temp;
        private boolean terminado = false;
        private boolean interrumpido = false;

        public WorkerServicioTB86000(PanelServicioTB8600 panel, TB86000 tb86000) {
            this.panel = panel;
            this.tb86000 = tb86000;
        }

        @Override
        protected Void doInBackground() throws Exception {
            System.out.println(" Lenvantando Hilo en el Panel de Servicio");
            while(!terminado){
                System.out.println(" entro en primer ciclo");
                while(!interrumpido){
                    rpm = tb86000.getRpm();
                    temp = tb86000.getTemp();
                    System.out.println("Logro Leer del Kit valores de Temp y RPM");
                    panel.getRadialRPM().setValue(rpm);
                    panel.getLinearTemperatura().setValue(temp);
                    panel.getTextAreaRPM().append("R: " + rpm +" T: " + temp+"\n");                    
                    Thread.sleep(200);
                }
                Thread.sleep(200);
            }
            return null;

    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public boolean isInterrumpido() {
        return interrumpido;
    }

    public void setInterrumpido(boolean interrumpido) {
        this.interrumpido = interrumpido;
    }

}