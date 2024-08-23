package org.soltelec.medicionrpm.centralauto;

import javax.swing.SwingWorker;






class WorkerServicioCentralAuto extends SwingWorker<Void,Void>{

        private PanelServicioCentral panel;
        private TB85000 tb85000;
        int rpm ,temp;
        private boolean terminado = false;
        private boolean interrumpido = false;

        public WorkerServicioCentralAuto(PanelServicioCentral panel, TB85000 tb85000) {
            this.panel = panel;
            this.tb85000 = tb85000;
        }

        @Override
        protected Void doInBackground() throws Exception {
            while(!terminado){

                while(!interrumpido){
                    rpm = tb85000.getRpm();
                    temp = tb85000.getTemp();
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