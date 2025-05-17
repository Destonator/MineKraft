public class Vector {
    double x = 0,y = 0,z = 0;
    public Vector(double x, double y, double z) {
        double length = Math.sqrt(x*x + y*y + z*z);
        if(length>0.0){
            this.x = x/length;
            this.y = y/length;
            this.z = z/length;//makes vector length of 1
        }

    }
    Vector CrossProduct(Vector v) {
        Vector newVector = new Vector(
                this.y* v.z - this.z*v.y,
                this.z*v.x - this.x*v.z,
                this.x*v.y - this.y*v.x);
        return newVector;
    }

    public double DotProduct(Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector normalize() {
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude == 0) {
            return new Vector(0, 0, 0); // Avoid division by zero
        }
        return new Vector(x / magnitude, y / magnitude, z / magnitude);
    }
}
