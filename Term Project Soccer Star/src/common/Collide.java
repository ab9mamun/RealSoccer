package common;



public class Collide extends Moving {
   private Player[] players;
   private Football ball;
    private double pMass, bMass;

    @Override
    public void setVisibility(double x, double y) {

    }

    public Collide(Player[] players, Football ball) {
        this.players = players;
        this.ball = ball;
        pMass = players[0].getMass();
        bMass = ball.getMass();
    }
    public void detectCollision(){
        for(int i=0; i<10; i++){
            detectCollision(players[i], ball);
            for(int j=i+1; j<10; j++){
                detectCollision(players[i],players[j]);
            }
        }
    }

   synchronized private void detectCollision(Player p1, Player p2){
        double x1, x2, y1, y2;
        x1 = p1.getCenterX();
        x2 = p2.getCenterX();
        y1 = p1.getCenterY();
        y2 = p2.getCenterY();
        if(findDistance2(x1, y1, x2, y2)<=(4*p1.getRadius()*p1.getRadius())){
            double angle = findAngle(x2, y2, x1, y1);
            p1.setCenter(p2.getCenterX()+p2.getRadius()*Math.cos(angle)+p1.getRadius()*Math.cos(angle)+2*Math.cos(angle),
                    p2.getCenterY()+p2.getRadius()*Math.sin(angle)+p1.getRadius()*Math.sin(angle)+2*Math.sin(angle));
            p2.setCenter(p1.getCenterX()+p1.getRadius()*Math.cos(angle+Math.PI)+p2.getRadius()*Math.cos(angle+Math.PI)+3*Math.cos(Math.PI+angle),
                    p1.getCenterY()+p1.getRadius()*Math.sin(Math.PI+angle)+p2.getRadius()*Math.sin(Math.PI+angle)+3*Math.sin(Math.PI+angle));
           /* x1 = p2.vx();
            x2 = p1.vx();
            y1 = p2.vy();
            y2 = p1.vy();
            p1.makeVelocity(x1, y1);
            p2.makeVelocity(x2, y2);*/

            double v1self, v2self, v1load,v2load, a1self, a2self, a1load,a2load;
            angle= (angle+Math.PI)%(2*Math.PI);
            a2load = angle;
            a1load = (angle+Math.PI)%(2*Math.PI);
            if(p1.angle>angle){
                a1self = angle+Math.PI/2;
            }
            else a1self = angle-Math.PI/2;
            v1self = p1.getVelocity()*Math.cos(a1self-p1.angle);
            v2load = p1.getVelocity()*Math.cos(p1.angle-a2load);
            if(p2.angle>a1load){
                a2self = a1load+Math.PI/2;
            }
            else a2self = a1load-Math.PI/2;
            v2self = p2.getVelocity()*Math.cos(a2self-p2.angle);
            v1load = p2.getVelocity()*Math.cos(p2.angle-a1load);

            p1.makeVelocity(v1self, a1self,v1load,a1load);
            p1.velocity*=GameState.collisionFactor;
            p2.makeVelocity(v2self, a2self,v2load,a2load);
            p1.velocity*=GameState.collisionFactor;

        }
    }
    private void detectCollision(Player p, Football b){
        double x1, x2, y1, y2;
        x1 = p.getCenterX();
        x2 = b.getCenterX();
        y1 = p.getCenterY();
        y2 = b.getCenterY();
        if(findDistance2(x1, y1, x2, y2)<=(p.getRadius()+b.getRadius())*(p.getRadius()+b.getRadius())) {
            double angle = findAngle(x2, y2, x1, y1);
            p.setCenter(b.getCenterX()+b.getRadius()*Math.cos(angle)+p.getRadius()*Math.cos(angle)+3*Math.cos(angle),
                    b.getCenterY()+b.getRadius()*Math.sin(angle)+p.getRadius()*Math.sin(angle)+3*Math.sin(angle) );
            b.setCenter(p.getCenterX()+p.getRadius()*Math.cos(angle+Math.PI)+b.getRadius()*Math.cos(Math.PI+angle)+3*Math.cos(Math.PI+angle),
                    p.getCenterY()+p.getRadius()*Math.sin(Math.PI+angle)+b.getRadius()*Math.sin(Math.PI+angle)+3*Math.sin(Math.PI+angle) );

            /*double vx1 = (p.vx() * (pMass - bMass) +
                    (2 * bMass * b.vx())) / (pMass + bMass);
            double vx2 = (b.vx() * (bMass - pMass) +
                    (2 * pMass * p.vx())) / (pMass + bMass);
            double vy1 = (p.vy() * (pMass - bMass) +
                    (2 * bMass * b.vy())) / (pMass + bMass);
            double vy2 = (b.vy() * (bMass - pMass) +
                    (2 * pMass * p.vy())) / (pMass + bMass);

            p.makeVelocity(vx1, vy1);
            b.makeVelocity(vx2, vy2);
            */
            double v1self, v2self, v1load,v2load, a1self, a2self, a1load,a2load;
            angle= (angle+Math.PI)%(2*Math.PI);
            a2load = angle;
            a1load = (angle+Math.PI)%(2*Math.PI);
            if(p.angle>angle){
                a1self = angle+Math.PI/2;
            }
            else a1self = angle-Math.PI/2;
            v1self = p.getVelocity()*Math.cos(a1self-p.angle);
            v2load = (pMass/bMass)*p.getVelocity()*Math.cos(p.angle-a2load);
            if(b.angle>a1load){
                a2self = a1load+Math.PI/2;
            }
            else a2self = a1load-Math.PI/2;
            v2self = b.getVelocity()*Math.cos(a2self-b.angle);
            v1load = (bMass/pMass)*b.getVelocity()*Math.cos(b.angle-a1load);

            p.makeVelocity(v1self, a1self,v1load,a1load);
            p.velocity*=GameState.collisionFactor;
            b.makeVelocity(v2self, a2self,v2load,a2load);
            b.velocity*=GameState.collisionFactor;
        }
    }
}

/*
newVelX1 = (firstBall.speed.x * (firstBall.mass – secondBall.mass) + (2 * secondBall.mass * secondBall.speed.x)) / (firstBall.mass + secondBall.mass);
newVelY1 = (firstBall.speed.y * (firstBall.mass – secondBall.mass) + (2 * secondBall.mass * secondBall.speed.y)) / (firstBall.mass + secondBall.mass);
newVelX2 = (secondBall.speed.x * (secondBall.mass – firstBall.mass) + (2 * firstBall.mass * firstBall.speed.x)) / (firstBall.mass + secondBall.mass);
newVelY2 = (secondBall.speed.y * (secondBall.mass – firstBall.mass) + (2 * firstBall.mass * firstBall.speed.y)) / (firstBall.mass + secondBall.mass);
 */