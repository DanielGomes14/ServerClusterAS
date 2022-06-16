package Server;

import Communication.Message;
import FIFO.IFIFO_Server;

import static Communication.MessageTopic.REPLY;
import static Communication.MessageTopic.REQUEST_PROCESSED;

class TComputeRequest extends Thread{


        private final IFIFO_Server mfifo;
        private  final String PI = "3.1415926589793";
        private final Server server;
        public  TComputeRequest(IFIFO_Server mfifo, Server server){
            this.mfifo = mfifo;
            this.server = server;
        }
        /**
         * Processing thread life cycle.
         */
        @Override
        public void run() {
            Message msg;
            while(true) {
                msg = mfifo.get();
                if(msg != null){
                    try {
                        Message reply = calculatePI(msg);
                        server.sendToClient(reply,reply.getServerPort());
                        reply.setTopic(REQUEST_PROCESSED);
                        server.sendtoMonitor(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        public  Message calculatePI(Message msg) throws InterruptedException {
                String base="3.";
                for(int i = 1; i<= msg.getNI();i++){
                    base = addDecimalPlace(base,i+1);
                }
                Thread.sleep(msg.getDeadline());
                // use the same message IG..
                msg.setTopic(REPLY);
                msg.setPi(Double.parseDouble(base));
                return msg;
        }

        public  String addDecimalPlace(String currentstr, int pos ){

            StringBuilder sb = new StringBuilder(currentstr);
            sb.insert(pos, PI.charAt(pos));
            return sb.toString();
        }





}
