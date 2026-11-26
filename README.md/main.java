
import java.util.*;

// ================ ENUM ================

enum OrderStatus {
    NEW, CONFIRMED, PAID, CANCELLED
}

enum PaymentType {
    CREDIT, INSTALLMENT
}

enum CreditRating {
    EXCELLENT, GOOD, AVERAGE, LOW
}

// ================ CUSTOMER ================

abstract class Customer {
    private int id;
    private String name;
    private int creditScore;

    public Customer(int id, String name, int creditScore) {
        if (id <= 0 || name.isBlank()
                || creditScore < 0 || creditScore > 1000) {
            throw new IllegalArgumentException("Клиент деректері қате");
        }

        this.id = id;
        this.name = name;
        this.creditScore = creditScore;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public abstract void checkout();
}

class PersonalCustomer extends Customer {
    private String cardNumber;

    public PersonalCustomer(int id, String name, int score, String card) {
        super(id, name, score);
        this.cardNumber = card;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public void checkout() {
        System.out.println(getName() + " тапсырыс рәсімдеді");
    }
}

class CorporateCustomer extends Customer {
    private String representative;
    private double creditLimit;

    public CorporateCustomer(int id, String name, int score,
                             String representative, double limit) {
        super(id, name, score);

        if (limit < 0 || representative.isBlank()) {
            throw new IllegalArgumentException("Компания деректері қате");
        }

        this.representative = representative;
        this.creditLimit = limit;
    }

    public String getRepresentative() {
        return representative;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    @Override
    public void checkout() {
        System.out.println(getName() + " компаниясы тапсырыс рәсімдеді");
    }
}

// ================ PRODUCT ================

class Product {
    private int id;
    private String name;
    private double price;

    public Product(int id, String name, double price) {
        if (id <= 0 || name.isBlank() || price <= 0) {
            throw new IllegalArgumentException("Тауар деректері қате");
        }

        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// ================ SELLER ================

class Seller {
    private int id;
    private String name;
    private List<Product> products = new ArrayList<>();

    public Seller(int id, String name) {
        if (id <= 0 || name.isBlank()) {
            throw new IllegalArgumentException("Сатушы деректері қате");
        }

        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addProduct(Product product) {
        if (findProduct(product.getId()) != null) {
            throw new IllegalArgumentException("Бұл ID қолданылған");
        }

        products.add(product);
    }

    public Product findProduct(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void showProducts() {
        System.out.println("\n=== ТАУАРЛАР ===");

        if (products.isEmpty()) {
            System.out.println("Тауар жоқ");
        }

        for (Product p : products) {
            System.out.println(
                    p.getId() + " | " + p.getName()
                            + " | " + p.getPrice() + " тг"
            );
        }
    }
}

// ================ ORDER ITEM ================

class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Тапсырыс деректері қате");
        }

        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double calculateSubtotal() {
        return product.getPrice() * quantity;
    }
}

// ================ CART ================

class Cart {
    private List<OrderItem> items = new ArrayList<>();

    public void addProduct(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Тауар саны қате");
        }

        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);

            if (item.getProduct().getId() == product.getId()) {
                items.set(i, new OrderItem(
                        product, item.getQuantity() + quantity
                ));
                return;
            }
        }

        items.add(new OrderItem(product, quantity));
    }

    public void removeProduct(int id) {
        boolean result = items.removeIf(
                item -> item.getProduct().getId() == id
        );

        System.out.println(result
                ? "Тауар өшірілді"
                : "Тауар табылмады");
    }

    public double getTotal() {
        double total = 0;

        for (OrderItem item : items) {
            total += item.calculateSubtotal();
        }

        return total;
    }

    public int getCount() {
        int count = 0;

        for (OrderItem item : items) {
            count += item.getQuantity();
        }

        return count;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void showCart() {
        System.out.println("\n=== СЕБЕТ ===");

        for (OrderItem item : items) {
            System.out.println(
                    item.getProduct().getName()
                            + " x " + item.getQuantity()
                            + " = " + item.calculateSubtotal() + " тг"
            );
        }

        System.out.println("Жалпы саны: " + getCount());
        System.out.println("Жалпы сома: " + getTotal() + " тг");
    }
}

// ================ ORDER ================

class Order {
    private int id;
    private Customer customer;
    private List<OrderItem> items;
    private double total;
    private OrderStatus status;

    public Order(int id, Customer customer, List<OrderItem> items) {
        if (id <= 0 || customer == null
                || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Тапсырыс қате");
        }

        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.NEW;

        calculateTotal();
    }

    private void calculateTotal() {
        total = 0;

        for (OrderItem item : items) {
            total += item.calculateSubtotal();
        }
    }

    public void updateStatus(OrderStatus newStatus) {
        boolean valid =
                (status == OrderStatus.NEW &&
                        (newStatus == OrderStatus.CONFIRMED
                                || newStatus == OrderStatus.CANCELLED))
                        ||
                        (status == OrderStatus.CONFIRMED &&
                                (newStatus == OrderStatus.PAID
                                        || newStatus == OrderStatus.CANCELLED));

        if (!valid) {
            throw new IllegalStateException("Статусты өзгерту мүмкін емес");
        }

        status = newStatus;
    }

    public int getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void showOrder() {
        System.out.println("\n=== ТАПСЫРЫС ===");
        System.out.println("ID: " + id);
        System.out.println("Клиент: " + customer.getName());

        for (OrderItem item : items) {
            System.out.println(
                    item.getProduct().getName()
                            + " x " + item.getQuantity()
                            + " = " + item.calculateSubtotal() + " тг"
            );
        }

        System.out.println("Жалпы сома: " + total + " тг");
        System.out.println("Статус: " + status);
    }
}

// ================ CREDIT SCORING ================

class CreditScoringService {

    public CreditRating checkScore(Customer customer) {
        int score = customer.getCreditScore();

        if (score >= 750) return CreditRating.EXCELLENT;
        if (score >= 700) return CreditRating.GOOD;
        if (score >= 600) return CreditRating.AVERAGE;

        return CreditRating.LOW;
    }

    public boolean approvePayment(Customer customer,
                                  PaymentType type) {
        if (type == PaymentType.CREDIT) {
            return customer.getCreditScore() >= 700;
        }

        return customer.getCreditScore() >= 650;
    }
}

// ================ PAYMENT ================

abstract class Payment {
    private double amount;

    public Payment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сома қате");
        }

        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public abstract void processPayment();
}

class StandardPayment extends Payment {
    public StandardPayment(double amount) {
        super(amount);
    }

    @Override
    public void processPayment() {
        System.out.println("Стандартты төлем өңделді: "
                + getAmount() + " тг");
    }
}

class CreditAgreement {
    private int id;
    private double amount;

    public CreditAgreement(int id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }
}

class CreditPayment extends Payment {
    private int months;
    private double rate;
    private CreditAgreement agreement;

    public CreditPayment(double amount, int months,
                         double rate, CreditAgreement agreement) {
        super(amount);

        if (months <= 0 || rate < 0 || agreement == null) {
            throw new IllegalArgumentException("Кредит шарты қате");
        }

        this.months = months;
        this.rate = rate;
        this.agreement = agreement;
    }

    @Override
    public void processPayment() {
        double monthly = getAmount() * (1 + rate) / months;

        System.out.println("Кредит мақұлданды");
        System.out.println("Келісім: " + agreement.getId());
        System.out.printf("Ай сайын: %.2f тг%n", monthly);
    }
}

class InstallmentPlan {
    private int id;
    private int months;

    public InstallmentPlan(int id, int months) {
        if (id <= 0 || months <= 0) {
            throw new IllegalArgumentException("Жоспар қате");
        }

        this.id = id;
        this.months = months;
    }

    public int getId() {
        return id;
    }
}

class InstallmentPayment extends Payment {
    private int months;
    private InstallmentPlan plan;

    public InstallmentPayment(double amount, int months,
                              InstallmentPlan plan) {
        super(amount);

        if (months <= 0 || plan == null) {
            throw new IllegalArgumentException("Рассрочка шарты қате");
        }

        this.months = months;
        this.plan = plan;
    }

    @Override
    public void processPayment() {
        System.out.println("Рассрочка мақұлданды");
        System.out.println("Жоспар: " + plan.getId());
        System.out.printf("Ай сайын: %.2f тг%n",
                getAmount() / months);
    }
}

// ================ MAIN ================

public class Main {

    static Scanner sc = new Scanner(System.in);
    static int nextOrderId = 101;

    static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Бүтін сан енгізіңіз!");
            }
        }
    }

    static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(
                        sc.nextLine().replace(',', '.')
                );
            } catch (NumberFormatException e) {
                System.out.println("Сан енгізіңіз!");
            }
        }
    }

    static String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine();

            if (!value.isBlank()) return value;

            System.out.println("Бос қалдыруға болмайды!");
        }
    }

    // Клиент тіркеу
    static Customer registerCustomer() {
        System.out.println("\n=== КЛИЕНТ ТІРКЕУ ===");

        int id = readInt("ID: ");
        String name = readText("Аты: ");
        int score = readInt("Кредит ұпайы: ");

        System.out.println("1. Жеке тұлға");
        System.out.println("2. Компания");

        int type = readInt("Таңдау: ");

        if (type == 1) {
            String card = readText("Карта нөмірі: ");

            return new PersonalCustomer(id, name, score, card);
        }

        if (type == 2) {
            String rep = readText("Өкіл аты: ");
            double limit = readDouble("Кредит лимиті: ");

            return new CorporateCustomer(
                    id, name, score, rep, limit
            );
        }

        throw new IllegalArgumentException("Клиент түрі қате");
    }

    // Тауар қосу
    static void addProducts(Seller seller) {
        int count = readInt("Қанша тауар қосасыз? ");

        for (int i = 0; i < count; i++) {
            System.out.println("\n" + (i + 1) + "-ТАУАР");

            int id = readInt("ID: ");
            String name = readText("Атауы: ");
            double price = readDouble("Бағасы: ");

            seller.addProduct(new Product(id, name, price));

            System.out.println("Тауар қосылды!");
        }
    }

    // Себетке тауар қосу
    static void addToCart(Seller seller, Cart cart) {
        seller.showProducts();

        int id = readInt("Тауар ID: ");
        Product product = seller.findProduct(id);

        if (product == null) {
            System.out.println("Тауар табылмады!");
            return;
        }

        int quantity = readInt("Саны: ");
        cart.addProduct(product, quantity);

        System.out.println("Себетке қосылды!");
    }

    // Төлем жасау
    static boolean makePayment(Customer customer, Order order) {
        CreditScoringService scoring = new CreditScoringService();

        System.out.println("\n=== КРЕДИТТІК СКОРИНГ ===");
        System.out.println("Рейтинг: " + scoring.checkScore(customer));

        System.out.println("\n1. Стандартты төлем");
        System.out.println("2. Кредит");
        System.out.println("3. Рассрочка");

        int choice = readInt("Төлем түрі: ");

        Payment payment;

        if (choice == 1) {
            payment = new StandardPayment(order.getTotal());
        } else if (choice == 2) {
            if (!scoring.approvePayment(customer, PaymentType.CREDIT)) {
                System.out.println("Кредит мақұлданбады!");
                return false;
            }

            int months = readInt("Мерзімі (ай): ");
            double rate = readDouble("Пайызы (0.15): ");

            payment = new CreditPayment(
                    order.getTotal(), months, rate,
                    new CreditAgreement(order.getId(), order.getTotal())
            );

        } else if (choice == 3) {
            if (!scoring.approvePayment(
                    customer, PaymentType.INSTALLMENT)) {
                System.out.println("Рассрочка мақұлданбады!");
                return false;
            }

            int months = readInt("Мерзімі (ай): ");

            payment = new InstallmentPayment(
                    order.getTotal(), months,
                    new InstallmentPlan(order.getId(), months)
            );

        } else {
            System.out.println("Төлем түрі қате!");
            return false;
        }

        payment.processPayment();
        return true;
    }

    // Тапсырыс жасау
    static void createOrder(Customer customer, Seller seller,
                            List<Order> history) {
        Cart cart = new Cart();

        while (true) {
            System.out.println("\n=== СЕБЕТ МӘЗІРІ ===");
            System.out.println("1. Тауар қосу");
            System.out.println("2. Себетті көрсету");
            System.out.println("3. Тауар өшіру");
            System.out.println("4. Тапсырысты рәсімдеу");
            System.out.println("0. Артқа");

            int choice = readInt("Таңдау: ");

            if (choice == 1) {
                addToCart(seller, cart);

            } else if (choice == 2) {
                cart.showCart();

            } else if (choice == 3) {
                int id = readInt("Тауар ID: ");
                cart.removeProduct(id);

            } else if (choice == 4) {
                if (cart.isEmpty()) {
                    System.out.println("Себет бос!");
                    continue;
                }

                Order order = new Order(
                        nextOrderId++, customer, cart.getItems()
                );

                customer.checkout();

                order.updateStatus(OrderStatus.CONFIRMED);

                if (makePayment(customer, order)) {
                    order.updateStatus(OrderStatus.PAID);
                }

                history.add(order);
                order.showOrder();
                return;

            } else if (choice == 0) {
                return;
            }
        }
    }

    // Тапсырыстар тарихы
    static void showHistory(List<Order> history) {
        System.out.println("\n=== ТАПСЫРЫСТАР ТАРИХЫ ===");

        if (history.isEmpty()) {
            System.out.println("Тапсырыс жоқ");
            return;
        }

        for (Order order : history) {
            order.showOrder();
        }
    }

    // Тапсырысты болдырмау
    static void cancelOrder(List<Order> history) {
        int id = readInt("Тапсырыс ID: ");

        for (Order order : history) {
            if (order.getId() == id) {
                try {
                    order.updateStatus(OrderStatus.CANCELLED);
                    System.out.println("Тапсырыс болдырылмады!");
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }

        System.out.println("Тапсырыс табылмады!");
    }

    // Негізгі бағдарлама
    public static void main(String[] args) {

        try {
            System.out.println("==============================");
            System.out.println("      KASPI MARKETPLACE");
            System.out.println("==============================");

            Customer customer = registerCustomer();

            Seller seller = new Seller(
                    readInt("Сатушы ID: "),
                    readText("Сатушы аты: ")
            );

            List<Order> history = new ArrayList<>();

            while (true) {
                System.out.println("\n========== МӘЗІР ==========");
                System.out.println("1. Тауар қосу");
                System.out.println("2. Тауарлар каталогы");
                System.out.println("3. Тапсырыс рәсімдеу");
                System.out.println("4. Тапсырыстар тарихы");
                System.out.println("5. Тапсырысты болдырмау");
                System.out.println("6. Клиент мәліметтері");
                System.out.println("0. Шығу");

                int choice = readInt("Таңдау: ");

                switch (choice) {
                    case 1:
                        addProducts(seller);
                        break;

                    case 2:
                        seller.showProducts();
                        break;

                    case 3:
                        createOrder(customer, seller, history);
                        break;

                    case 4:
                        showHistory(history);
                        break;

                    case 5:
                        cancelOrder(history);
                        break;

                    case 6:
                        System.out.println("ID: " + customer.getId());
                        System.out.println("Аты: " + customer.getName());
                        System.out.println("Кредит ұпайы: "
                                + customer.getCreditScore());
                        break;

                    case 0:
                        System.out.println("Бағдарлама аяқталды!");
                        return;

                    default:
                        System.out.println("Қате таңдау!");
                }
            }

        } catch (Exception e) {
            System.out.println("ҚАТЕ: " + e.getMessage());
        }
    }
}
