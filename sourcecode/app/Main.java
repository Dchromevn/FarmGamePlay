package app;

import controller.*;
import core.Farm;
import eventSystem.RandomEventManager;
import player.Player;
import utility.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RandomEventManager eventManager = new RandomEventManager();
        Player player = new Player();
        Farm farm = new Farm(5, 5);  // 5x5 farm
        PlayerController controller = new PlayerController(player, farm);

        System.out.println("Welcome to SmartFarm!");

        while (true) {
            System.out.println("\n===== SMART FARM MENU =====");
            System.out.println("1. Plant Crop");
            System.out.println("2. Water Crop");
            System.out.println("3. Fertilize Crop");
            System.out.println("4. Harvest Crop");
            System.out.println("5. Buy Seeds");
            System.out.println("6. View Inventory");
            System.out.println("7. Next Day");
            System.out.println("8. View Farm");
            System.out.println("9. View Player Info");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("Goodbye!");
                break;
            }

            switch (choice) {

                case 1: 
                    System.out.println("Available crops: ");
                    for (CropType t : CropType.values())
                        System.out.println("- " + t);

                    System.out.print("Enter crop type: ");
                    String typeInput = scanner.next().toUpperCase();
                    CropType type = CropType.valueOf(typeInput);

                    System.out.print("Enter X: ");
                    int x = scanner.nextInt();

                    System.out.print("Enter Y: ");
                    int y = scanner.nextInt();

                    boolean planted = controller.plantCrop(type, new Point(x, y));
                    System.out.println(planted ? "Planted!" : "Failed to plant.");
                    break;

                case 2:
                    System.out.print("Enter X: ");
                    x = scanner.nextInt();
                    System.out.print("Enter Y: ");
                    y = scanner.nextInt();

                    boolean watered = controller.waterCrop(new Point(x, y), 10);
                    System.out.println(watered ? "Watered!" : "Failed to water.");
                    break;

                case 3:
                    System.out.print("Enter X: ");
                    x = scanner.nextInt();
                    System.out.print("Enter Y: ");
                    y = scanner.nextInt();

                    boolean fertilized = controller.fertilizeCrop(new Point(x, y), 5);
                    System.out.println(fertilized ? "Fertilized!" : "Failed to fertilize.");
                    break;

                case 4: 
                    System.out.print("Enter X: ");
                    x = scanner.nextInt();
                    System.out.print("Enter Y: ");
                    y = scanner.nextInt();

                    boolean harvested = controller.harvestCrop(new Point(x, y));
                    System.out.println(harvested ? "Harvested!" : "Cannot harvest.");
                    break;

                case 5: 
                    System.out.println("Available seeds: ");
                    for (CropType t : CropType.values())
                        System.out.println(t + " : " + t.getSeedPrice() + " coins");

                    System.out.print("Choose seed type: ");
                    typeInput = scanner.next().toUpperCase();
                    type = CropType.valueOf(typeInput);

                    System.out.print("Amount: ");
                    int amount = scanner.nextInt();

                    boolean bought = controller.buySeed(type, amount);
                    System.out.println(bought ? "Purchased!" : "Not enough money!");
                    break;
                case 6: 
                	controller.displayInventory();
                	break;

                case 7: 
                	farm.advanceDay(eventManager);
                    break;

                case 8: 
                    controller.printFarmStatus();
                    break;
                case 9: 
                    controller.printPlayerStatus();
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }
}
