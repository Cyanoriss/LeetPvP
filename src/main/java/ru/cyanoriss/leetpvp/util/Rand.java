package ru.cyanoriss.leetpvp.util;

import org.bukkit.Location;

import java.util.Random;

public class Rand {
    private static final Random random = new Random();

    /**
     * Определяет выполняемость чего-то
     * @param percent шанс выполнения
     * @return выполнится или нет
     */
    public static boolean chance(double percent) {
        return percent >= random.nextDouble() * 100;
    }

    /**
     * Генерирует случайное рациональное число
     * @param number1 От какого числа генерировать
     * @param number2 До какого числа генерировать
     * @return Сгенерированное число
     */
    public static double between(double number1, double number2) {
        return random.nextDouble() * (number2 - number1) + number1;
    }

    /**
     * Генерирует случайное целое число
     * @param number1 От какого числа генерировать
     * @param number2 До какого числа генерировать
     * @return Сгенерированное число
     */
    public static int between(int number1, int number2) {
        return random.nextInt(number2 - number1) + number1;
    }

    /**
     * Генерирует случайную локацию
     * @param center Центр
     * @param side Длина стороны
     * @return Случайная локация
     */
    public static Location location(Location center, int side) {
        int minX = center.getBlockX() - side / 2;
        int maxX = center.getBlockX() + side / 2;
        int minZ = center.getBlockZ() - side / 2;
        int maxZ = center.getBlockZ() + side / 2;

        int randomX = (int) (Math.random() * (maxX - minX)) + minX;
        int randomZ = (int) (Math.random() * (maxZ - minZ)) + minZ;

        return new Location(center.getWorld(), randomX, center.getY(), randomZ).toHighestLocation();
    }

    /**
     * Генерирует случайную локацию
     * @param center Центр
     * @param side1 Минимальный радиус
     * @param side2 Максимальный радиус
     * @return Случайная локация
     */
    public static Location location(Location center, int side1, int side2) {
        double randX = between(side1, side2);
        if (chance(50)) randX *= -1;

        double randZ = between(side1, side2);
        if (chance(50)) randZ *= -1;

        return new Location(center.getWorld(), randX + center.getBlockX(), 0, randZ + center.getBlockZ()).toHighestLocation();
    }

    public static void lookAt(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double yaw = (float) Math.toDegrees(Math.atan2(dz, dx));
        double pitch = (float) Math.toDegrees(Math.atan(dy / distance));

        from.setYaw((float) normalizeYaw(yaw));
        from.setPitch((float) normalizePitch(pitch));
    }

    private static double normalizeYaw(double yaw) {
        yaw %= 360;
        if (yaw < 0) {
            yaw += 360;
        }
        return yaw;
    }

    private static double normalizePitch(double pitch) {
        pitch %= 360;
        if (pitch > 90) {
            pitch = 90;
        } else if (pitch < -90) {
            pitch = -90;
        }
        return pitch;
    }
}
