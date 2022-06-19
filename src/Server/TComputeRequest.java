package Server;

import Communication.Message;
import Communication.MessageTopic;
import FIFO.IFIFO_Server;

import static Communication.MessageTopic.REPLY;
import static Communication.MessageTopic.REQUEST_PROCESSED;

class TComputeRequest extends Thread{
        private final IFIFO_Server mfifo;
        private  final String PI = "3.1415926589793";
        private final Server server;
        private boolean end;

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
                        msg.setServerId(server.getServerId());
                        msg.setTopic(MessageTopic.REQUEST_IN_PROCESS);
                        server.sendtoMonitor(msg);
                        server.getGui().inProcessingRequest(msg);

                        Message reply = calculatePI(msg);
                        if(isEnd()) break;
                        System.out.println("Still processing....");
                        System.out.println(reply.getServerPort());

                        server.sendToClient(reply, reply.getServerPort());
                        System.out.println("Still processing1....");

                        msg.setTopic(REQUEST_PROCESSED);
                        server.sendtoMonitor(msg);

                        server.getGui().addReply(reply);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public  Message calculatePI(Message msg) throws InterruptedException {
                String base="3.";
                for(int i = 1; i<=msg.getNI(); i++){
                    base = addDecimalPlace(base,i+1);
                }
                Message reply = new Message(REPLY, msg.getRequestId(), msg.getServerId(), msg.getNI(), msg.getDeadline());
                reply.setClientId(msg.getClientId());
                Thread.sleep(reply.getNI() * 2000);
                reply.setServerPort(msg.getServerPort());
                reply.setPi(Double.parseDouble(base));
                return reply;
        }

        public  String addDecimalPlace(String currentstr, int pos ){
            StringBuilder sb = new StringBuilder(currentstr);
            sb.insert(pos, PI.charAt(pos));
            return sb.toString();
        }

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }
}
