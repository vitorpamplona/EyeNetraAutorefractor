/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.core.testdevice;

import com.vitorpamplona.core.utils.AngleDiff;

import java.util.Random;

public class Particle {

    public float color;
    public float size;
    public float initalSize;
    public float x;
    public float y;
    Random r = new Random();

    private double velocity;
    private double direction;
    public float accelaration;

    private double cosDirection;
    private double sinDirection;

    public Particle(int x, int y, float radius) {
        init(x, y);
        this.color = 1;
        this.initalSize = (float) ((Math.pow(r.nextFloat(), 3)) * radius);
        if (this.initalSize < 1)
            this.initalSize = 1;

        this.velocity = 0.5f;
        this.accelaration = 1.002f;
    }

    public void init(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocity = 0.5f;

        this.direction = 2 * Math.PI * r.nextDouble();

        while (!isLegalDirection(direction)) {
            this.direction = 2 * Math.PI * r.nextDouble();
        }

        cosDirection = Math.cos(direction);
        sinDirection = Math.sin(direction);

        this.x = (float) (500 * cosDirection);
        this.y = (float) (500 * sinDirection);

        cosDirection = -cosDirection;
        sinDirection = -sinDirection;
    }

    public boolean isLegalDirection(double direction) {
        float degrees = (float) Math.toDegrees(direction);

        return AngleDiff.diff180(degrees, 0) > 10
                && AngleDiff.diff180(degrees, 45) > 10
                && AngleDiff.diff180(degrees, 135) > 10;
    }

    public synchronized void move(double deltaTime) {
        double normalizedTime = deltaTime / 10;

        this.velocity = this.velocity * accelaration;

        x = (float) (x + (cosDirection * this.velocity * normalizedTime));
        y = (float) (y + (sinDirection * this.velocity * normalizedTime));

        // attaches x and y to depth;
        float depth = (float) (Math.sqrt(x * x + y * y) / 200.f);

        this.size = this.initalSize * (depth);
    }

}