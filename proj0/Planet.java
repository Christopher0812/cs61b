public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;

	public Planet(double xP, double yP, double xV, double yV, double m, String img) {
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}

	public Planet(Planet p) {
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}

	public double calcDistance(Planet p) {
		double dx = Math.abs(p.xxPos - xxPos);
		double dy = Math.abs(p.yyPos - yyPos);
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double calcForceExertedBy(Planet p) {
		double r = calcDistance(p);
		double G = 6.67e-11;
		return G * p.mass * mass / (r * r);
	}

	public double calcForceExertedByX(Planet planet) {
		double dx = planet.xxPos - xxPos;
		double r = calcDistance(planet);
		double F = calcForceExertedBy(planet);
		return F * dx / r;
	}

	public double calcForceExertedByY(Planet planet) {
		double dy = planet.yyPos - yyPos;
		double r = calcDistance(planet);
		double F = calcForceExertedBy(planet);
		return F * dy / r;
	}

	public double calcNetForceExertedByX(Planet[] planets) {
		double totalX = 0;

		for (Planet p : planets) {
			if (this.equals(p)) {
				continue;
			} else {
				totalX += calcForceExertedByX(p);
			}
		}
		return totalX;
	}

	public double calcNetForceExertedByY(Planet[] planets) {
		double totalY = 0;

		for (Planet p : planets) {
			if (this.equals(p)) {
				continue;
			} else {
				totalY += calcForceExertedByY(p);
			}
		}
		return totalY;
	}

	public void update(double dt, double xForce, double yForce) {
		double ax = xForce / mass;
		double ay = yForce / mass;
		xxVel = xxVel + dt * ax;
		yyVel = yyVel + dt * ay;
		xxPos = xxPos + dt * xxVel;
		yyPos = yyPos + dt * yyVel;
	}

	/** Make the planet able to draw itself at the right position */
	public void draw() {
		StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
	}
}