package app;

import controller.*;
import core.Farm;
import eventSystem.RandomEventManager;
import notification.Notification;
import notification.NotificationManager;
import player.Player;
import resourceManagement.ResourceManager;
import resourceManagement.Store;
import utility.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RandomEventManager eventManager = new RandomEventManager();
        Player player = new Player();
        Farm farm = new Farm(5, 5);// 5x5 farm
        ResourceManager shopLogic = new ResourceManager();
        NotificationManager notificationManager= new NotificationManager();
        PlayerController controller = new PlayerController(player, farm, notificationManager,shopLogic);
        Store store=new Store(controller);
        System.out.println("Welcome to SmartFarm!");

        while (true) {
            System.out.println("\n===== SMART FARM MENU =====");
            System.out.println("1. Plant Crop");
            System.out.println("2. Water Crop");
            System.out.println("3. Fertilize Crop");
            System.out.println("4. Harvest Crop");
            System.out.println("5. View Store");
            System.out.println("6. View Inventory");
            System.out.println("7. Next Day");
            System.out.println("8. View Farm");
            System.out.println("9. View Notification");
            System.out.println("10. View Plant Status");
            System.out.println("11. Recycle Crop");
            System.out.println("12. Recover Crop");


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
                    System.out.print("Enter amount: ");
                    int amount = scanner.nextInt();
                    boolean watered = controller.waterCrop(new Point(x, y), amount);
                    System.out.println(watered ? "Watered!" : "Failed to water.");
                    break;

                case 3:
                    System.out.print("Enter X: ");
                    x = scanner.nextInt();
                    System.out.print("Enter Y: ");
                    y = scanner.nextInt();
                    System.out.print("Enter amount: ");
                    amount = scanner.nextInt();
                    boolean fertilized = controller.fertilizeCrop(new Point(x, y), amount);
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
                    store.showStoreDialog();
                    break;
                case 6:
                    controller.displayInventory();
                    break;

                case 7:
                    String result= farm.advanceDay(eventManager);
                    if (result != null) {
                        notificationManager.addNotification(
                                result,
                                NotificationType.EVENT,
                                farm.getCurrentDay()
                        );
                    } else {
                        notificationManager.addNotification(
                                "Just a normal day",
                                NotificationType.INFO,
                                farm.getCurrentDay()
                        );
                    }
                    break;

                case 8:
                    controller.printFarmStatus();
                    break;
                case 9:
                    System.out.println("Notification:");
                    List<Notification> allNotifications = notificationManager.getAllNotifications();

                    if (allNotifications.isEmpty()) {
                        System.out.println("There is no notification");
                    } else {
                        for (Notification n : allNotifications) {
                            System.out.println(n.toString());
                        }
                    }
                    break;
                case 10:
                    System.out.print("Enter X: "); int sx = scanner.nextInt();
                    System.out.print("Enter Y: "); int sy = scanner.nextInt();
                    controller.showCropStatus(new Point(sx, sy));
                    break;

                case 11:
                    System.out.print("Enter X: "); int rx = scanner.nextInt();
                    System.out.print("Enter Y: "); int ry = scanner.nextInt();
                    controller.recycleCrop(new Point(rx, ry));
                    break;
                case 12:
                	System.out.print("Enter X: ");
                	int cx = scanner.nextInt();
                	System.out.print("Enter Y: ");
                	int cy = scanner.nextInt();
                	controller.cureCrop(new Point(cx, cy));
                	break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }
}