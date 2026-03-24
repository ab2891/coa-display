package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Engine for guessing burrow position from spade lava particles.
 *
 * <p>Based on SBO-Kotlin's PreciseGuessBurrow approach: the spade shoots lava
 * particles along a cubic Bezier-like curve from the player toward the burrow.
 * By fitting cubic polynomials (degree-3) independently to x(t), y(t), z(t),
 * we can extrapolate the endpoint (t=1) to estimate the burrow position.</p>
 *
 * <h3>Algorithm outline:</h3>
 * <ol>
 *   <li>Collect lava particle positions in order (they arrive along the curve).</li>
 *   <li>Assign each particle a normalized parameter t in [0, 1] based on index.</li>
 *   <li>Fit cubic polynomials x(t), y(t), z(t) via least-squares regression.</li>
 *   <li>Evaluate at t = 1.0 to get the extrapolated burrow position.</li>
 *   <li>The pitch of the launch determines distance -- binary search over pitch
 *       angles accounting for the 0.75 gravity offset gives the distance estimate.</li>
 * </ol>
 */
@Environment(EnvType.CLIENT)
public class BurrowGuessEngine {

    private static final double GRAVITY_OFFSET = 0.75;
    private static final int POLYNOMIAL_DEGREE = 3;

    /**
     * Estimate burrow position from spade lava particle positions.
     *
     * @param lavaParticles ordered list of lava particle positions
     * @return estimated burrow block position, or null if not enough data
     */
    public BlockPos estimateBurrow(List<Vec3d> lavaParticles) {
        if (lavaParticles.size() < 4) {
            return null;
        }

        int n = lavaParticles.size();
        double[] tValues = new double[n];
        for (int i = 0; i < n; i++) {
            tValues[i] = (double) i / (n - 1);
        }

        double[] xVals = new double[n];
        double[] yVals = new double[n];
        double[] zVals = new double[n];
        for (int i = 0; i < n; i++) {
            Vec3d p = lavaParticles.get(i);
            xVals[i] = p.x;
            yVals[i] = p.y;
            zVals[i] = p.z;
        }

        double[] xCoeffs = fitPolynomial(tValues, xVals, POLYNOMIAL_DEGREE);
        double[] yCoeffs = fitPolynomial(tValues, yVals, POLYNOMIAL_DEGREE);
        double[] zCoeffs = fitPolynomial(tValues, zVals, POLYNOMIAL_DEGREE);

        if (xCoeffs == null || yCoeffs == null || zCoeffs == null) {
            return null;
        }

        double endX = evalPolynomial(xCoeffs, 1.0);
        double endY = evalPolynomial(yCoeffs, 1.0);
        double endZ = evalPolynomial(zCoeffs, 1.0);

        return new BlockPos((int) Math.floor(endX), (int) Math.floor(endY), (int) Math.floor(endZ));
    }

    /**
     * Estimate distance from pitch using binary search, accounting for gravity.
     * The lava particle arc has a 0.75 block gravity offset per unit distance.
     *
     * @param pitchDeg pitch angle in degrees (negative = upward)
     * @return estimated horizontal distance to burrow
     */
    public double estimateDistanceFromPitch(double pitchDeg) {
        double pitchRad = Math.toRadians(-pitchDeg);
        double lo = 10.0;
        double hi = 1500.0;

        for (int i = 0; i < 50; i++) {
            double mid = (lo + hi) / 2.0;
            double expectedPitch = Math.atan2(GRAVITY_OFFSET * mid, mid);
            if (pitchRad > expectedPitch) {
                hi = mid;
            } else {
                lo = mid;
            }
        }
        return (lo + hi) / 2.0;
    }

    /**
     * Fit a polynomial of the given degree using least-squares (normal equations).
     * Returns coefficients [a0, a1, ..., aDeg] such that f(t) = a0 + a1*t + a2*t^2 + ...
     */
    private static double[] fitPolynomial(double[] t, double[] y, int degree) {
        int n = t.length;
        int m = degree + 1;
        double[][] A = new double[m][m];
        double[] b = new double[m];

        for (int i = 0; i < n; i++) {
            double[] powers = new double[2 * m];
            powers[0] = 1.0;
            for (int j = 1; j < 2 * m; j++) {
                powers[j] = powers[j - 1] * t[i];
            }
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < m; c++) {
                    A[r][c] += powers[r + c];
                }
                b[r] += powers[r] * y[i];
            }
        }

        return solveLinearSystem(A, b);
    }

    /**
     * Solve a small linear system Ax = b using Gaussian elimination with partial pivoting.
     */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }

        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[maxRow][col])) {
                    maxRow = row;
                }
            }
            double[] tmp = aug[col];
            aug[col] = aug[maxRow];
            aug[maxRow] = tmp;

            if (Math.abs(aug[col][col]) < 1e-12) {
                return null;
            }

            for (int row = col + 1; row < n; row++) {
                double factor = aug[row][col] / aug[col][col];
                for (int j = col; j <= n; j++) {
                    aug[row][j] -= factor * aug[col][j];
                }
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= aug[i][j] * x[j];
            }
            x[i] /= aug[i][i];
        }
        return x;
    }

    private static double evalPolynomial(double[] coeffs, double t) {
        double result = 0.0;
        double power = 1.0;
        for (double c : coeffs) {
            result += c * power;
            power *= t;
        }
        return result;
    }
}
