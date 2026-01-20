package com.azizul.test;
import java.util.*;

public class Main {

    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    enum ClassType { BARBARIAN, WIZARD, ARCHER, BARD }
    enum EnemyType { BOAR, ORC, UNDEAD, DEMON }

    static ClassType chooseClass() {
        System.out.println("Choose your class:");
        System.out.println("1. Barbarian");
        System.out.println("2. Wizard");
        System.out.println("3. Archer");
        System.out.println("4. Bard");

        int choice = scanner.nextInt();

        return switch (choice) {
            case 1 -> ClassType.BARBARIAN;
            case 2 -> ClassType.WIZARD;
            case 3 -> ClassType.ARCHER;
            case 4 -> ClassType.BARD;
            default -> {
                System.out.println("Invalid choice. Defaulting to Barbarian.");
                yield ClassType.BARBARIAN;
            }
        };
    }
    static class Character {
        String name;
        ClassType classType;
        int level = 1;
        int maxHealth;
        int health;
        int maxMana;
        int mana;
        int exp = 0;
        int speed;
        int baseDamage;

        Character(String name, ClassType chosenClass) {
            this.name = name;
            this.classType = chosenClass;
            generateStats();
        }

        public Character(String ally1) {
            this.name = ally1;
        }

        void generateStats() {
            maxHealth = random.nextInt(6) + 15;
            health = maxHealth;
            maxMana = random.nextInt(6) + 10;
            mana = maxMana;

            switch (classType) {
                case BARBARIAN -> { speed = 3; baseDamage = 8; }
                case WIZARD -> { speed = 4; baseDamage = 6; }
                case ARCHER -> { speed = 5; baseDamage = 7; }
                case BARD -> { speed = 4; baseDamage = 5; }
            }
        }

        int dealDamage() {
            return Math.max(1, random.nextInt(baseDamage - (baseDamage - 3) + 1) + (baseDamage - 3));
        }

        boolean dodge() {
            return random.nextInt(100) < 10;
        }

        boolean flee() {
            return random.nextInt(100) < 5;
        }

        void gainExp() {
            exp += 50;
            if (exp >= level * 100 && level < 10) {
                level++;
                exp = 0;
                maxHealth += 5;
                health = maxHealth;
                System.out.println(name + " leveled up to " + level + "!");
            }
        }
    }

    static class Enemy {
        EnemyType type;
        int level;
        int maxHealth;
        int health;
        int damage;

        Enemy() {
            type = EnemyType.values()[random.nextInt(4)];
            level = random.nextInt(10) + 1;
            maxHealth = random.nextInt(11) + 10;
            health = maxHealth;

            damage = switch (type) {
                case BOAR-> 3;
                case ORC -> 4;
                case UNDEAD -> 3;
                case DEMON -> 5;
            };
        }
    }

    static void fight(Character c, Enemy e) {
        System.out.println("\nFight between " + c.classType + "(lv" + c.level + ") and " + e.type + "(lv" + e.level + ")\n");

        while (c.health > 0 && e.health > 0) {

            System.out.println("Level " + c.level + " " + c.classType + " attacks " + e.type);
            int dmg = c.dealDamage();
            e.health -= dmg;
            System.out.println(e.type + " takes " + dmg + " DMG");

            if (e.health <= 0) {
                System.out.println(e.type + " has been slain");
                System.out.println(c.classType + " Wins");
                c.gainExp();
                return;
            }

            System.out.println(e.type + " charges at Level " + c.level + " " + c.classType);

            if (c.dodge()) {
                System.out.println(c.classType + " dodges!");
                continue;
            }

            c.health -= e.damage;
            System.out.println(c.classType + " loses " + e.damage + " HP");

            if (c.health <= 0) {
                System.out.println(c.classType + " has fallen!");
                return;
            }
        }
    }

    static void groupFight(List<Character> team, Enemy enemy) {
        System.out.println("\nGroup battle begins!");

        while (!team.isEmpty() && enemy.health > 0) {

            for (Iterator<Character> it = team.iterator(); it.hasNext(); ) {
                Character c = it.next();

                int dmg = c.dealDamage();
                enemy.health -= dmg;
                System.out.println(c.classType + " deals " + dmg + " DMG");

                if (enemy.health <= 0) {
                    System.out.println(enemy.type + " defeated!");
                    return;
                }

                if (!c.dodge()) {
                    c.health -= enemy.damage;
                    System.out.println(enemy.type + " hits " + c.classType);

                    if (c.health <= 0) {
                        System.out.println(c.classType + " is defeated!");
                        it.remove();
                    } else if (c.flee()) {
                        System.out.println(c.classType + " flees!");
                        it.remove();
                    }
                } else {
                    System.out.println(c.classType + " dodges!");
                }
            }
        }

        System.out.println("Enemy Wins!");
    }
    static void main(String[] args) {

        System.out.println("Enter your character name: ");
        String name = scanner.next();
        
        ClassType chosenClass = chooseClass();
        Character player = new Character(name, chosenClass);

        while (true) {
            System.out.println("\n--- RPG MENU ---");
            System.out.println("1. Show Character");
            System.out.println("2. Fight Enemy");
            System.out.println("3. Group Fight");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.println("\nClass: " + player.classType);
                    System.out.println("Level: " + player.level);
                    System.out.println("HP: " + player.health + "/" + player.maxHealth);
                    System.out.println("Mana: " + player.mana + "/" + player.maxMana);
                }
                case 2 -> fight(player, new Enemy());
                case 3 -> {
                    List<Character> team = new ArrayList<>();
                    team.add(player);
                    team.add(new Character("Ally1"));
                    team.add(new Character("Ally2"));
                    groupFight(team, new Enemy());
                }
                case 4 -> System.exit(0);
            }
        }
    }
}