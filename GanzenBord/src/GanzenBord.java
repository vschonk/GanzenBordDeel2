import java.io.Console;
import java.util.Scanner;
import java.util.Random;

public class GanzenBord {
    public static void main(String[] args) {
        Spel spel = new Spel();
        spel.spelen();
    }
}

class Spel {
    int totalDice;
    Scanner input = new Scanner(System.in);
    char inputChar;
    boolean spelGewonnen;
    Dice dice1 = new Dice();
    Dice dice2 = new Dice();
    Board board = new Board();
    Player[] players;

    void spelen() {
        while (!spelGewonnen) {
            for (int player = 0; player < players.length; player++) {
                if (players[player].skipTurn || players[player].opgesloten) {
                    System.out.println("Sorry maar speler " + players[player].colour + " moet zijn beurt overslaan!");
                    players[player].skipTurn = false;
                    continue;
                }
                players[player].originalPosition = players[player].position;
                System.out.println("\uD83E\uDD86"+ConsoleColors.BLUE_UNDERLINED+"Speler " + players[player].playerNumber + " (" + players[player].colour + ")" + " op positie " + players[player].position + " is aan de beurt!"+ConsoleColors.RESET);
                System.out.println(ConsoleColors.YELLOW_BOLD+"Gooi met G en stop programma met X."+ConsoleColors.RESET);
                try {
                    inputChar = input.next().charAt(0);
                    if (inputChar == 'G') {
                        totalDice = dice1.diceThrow() + dice2.diceThrow();
                        System.out.println("Uw worp is " + dice1.lastThrow + " + " + dice2.lastThrow + " dus totaal: " + totalDice); //+ " DEBUG:Het verwachte vakje is " + (players[player].position + totalDice));
                        if (players[player].firstTurn) {
                            if (players[player].firstTurn && totalDice == 9) {
                                eersteBeurt(players[player], board.vakjes[53], board.vakjes[26], dice2, dice1);
                                players[player].firstTurn = false;
                                continue;
                            } else {
                                players[player].firstTurn = false;
                            }
                        } else players[player].firstTurn = false;
                        checkLast(players, players[player]);
                        if (players[player].position + totalDice <= 63) {
                            astralProjection(players, players[player], dice1, dice2);
                            players[player].position += totalDice;
                            //System.out.println("Debug: IF <=63 is uitgevoerd.");
                        } else {
                            totalDice = (63 - (players[player].position + totalDice));
                            astralProjection(players, players[player], dice1, dice2);
                            players[player].position = 63 + totalDice;

                            //System.out.println("DEBUG: Else is uitgevoerd.");
                        }
                        System.out.println("Speler " + players[player].colour + " staat op vakje " + players[player].position + ".");
                        switch (players[player].position) {
                            case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59:
                                astralProjection(players, players[player], dice1, dice2);
                                board.vakjes[players[player].position].uitvoeren(players[player], dice1, dice2, board.vakjes[player]);
                            default:
                                board.vakjes[players[player].position].uitvoeren(players[player], dice1, dice2);

                        }
                        board.vakjes[players[player].originalPosition].bezet = false;
                        board.vakjes[players[player].originalPosition].icoon = board.vakjes[players[player].originalPosition].trueIcoon;
                        board.vakjes[players[player].position].bezet = true;
                        board.vakjes[players[player].position].icoon = (char) (players[player].playerNumber + '0');

                    }
                    board.renderBoard();
                    if (inputChar == 'X') {
                        System.out.println("RAGEQUIT.");
                        System.exit(1337);
                    }
                } catch (Exception e) {
                    System.out.println("Alleen G of X doen iets, nerd.");
                }
            }
        }
    }

    private void checkLast(Player[] players, Player p) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].playerNumber == p.playerNumber)
                continue;
            if (players[i].position < p.position) {
                p.lastplace = false;
                break;
            } else p.lastplace = true;
        }
    }

    private void astralProjection(Player[] players, Player p, Dice dice1, Dice dice2) {
        int totalMoves = dice1.lastThrow + dice2.lastThrow;
        //System.out.println("Astral Projection aangeroepen.");
        for (int i = p.position; i < p.position + totalMoves; i++) {
            //System.out.println("Astrale gans van speler " + p.playerNumber + " op vakje " + i);
            if (i == 63) {
                int reversePos = (63 - (p.position + totalMoves));
                //System.out.println("Astrale gans op vakje 63, stop en omdraaien!");
                for (int y = 63; y > reversePos + 63; y--)
                    //System.out.println("Astrale gans van speler " + p.playerNumber + " op vakje " + y);
                    for (int z = 0; z < players.length; z++) {
                        if (players[z].position == i && players[z].opgesloten) {
                            System.out.println("Speler " + players[z].playerNumber + ", " + players[z].colour + " is gered!");
                            players[z].opgesloten = false;
                        }
                    }
                break;
            }
            for (int y = 0; y < players.length; y++) {
                if (players[y].position == i && players[y].opgesloten) {
                    System.out.println("Speler " + players[y].playerNumber + ", " + players[y].colour + " is gered!");
                    players[y].opgesloten = false;
                }
            }
        }
    }

    public void eersteBeurt(Player p, Vakjes vakje53, Vakjes vakje26, Dice dice1, Dice dice2) {
        int diceTotal = dice1.lastThrow + dice2.lastThrow;
        if (diceTotal == 9) {
            if (dice1.lastThrow == 5 || dice1.lastThrow == 4) {
                if (vakje53.bezet) {
                    System.out.println("Mooie 9 worp maar... dat vakje is al bezet!");
                    p.firstTurn = false;
                    board.renderBoard();
                } else {
                    p.position = 53;
                    p.firstTurn = false;
                    System.out.println("Eerste beurt 9 met " + dice1.lastThrow + " en " + dice2.lastThrow + "!" + p.playerNumber + " Gaat naar vakje 53");
                    vakje53.icoon = (char) (p.playerNumber);
                    board.renderBoard();
                }
            }
            if (dice1.lastThrow == 6 || dice1.lastThrow == 3) {
                if (vakje26.bezet) {
                    System.out.println("Mooie worp, jammer maar vakje 26 is al bezet!");
                    board.renderBoard();

                } else {
                    p.position = 26;
                    p.firstTurn = false;
                    System.out.println("Eerste beurt 9 met " + dice1.lastThrow + " en " + dice2.lastThrow + "! Speler " + p.playerNumber + " Gaat naar vakje 53");
                    vakje26.icoon = (char) (p.playerNumber);
                    board.renderBoard();
                }
            } else
                p.firstTurn = false;
            board.renderBoard();
        }
    }

    Spel() {
        System.out.println("Hoeveel Spelers?");
        boolean correctSpelers = false;
        int playersExpected = 0;
        playersExpected = input.nextInt();
        do {
            if (playersExpected > 6) {
                System.out.println("Mag alleen lager dan 7, pannenkoek. Vul dat maar es in.");
                playersExpected = input.nextInt();
            }
            if (playersExpected < 0) {
                System.out.println("Grapjas.");
                playersExpected = input.nextInt();
            } else correctSpelers = true;
        }
        while (!correctSpelers);
        System.out.println("U gaat spelen met " + playersExpected + " spelers.");

        int spelerNummer;
        players = new Player[playersExpected];

        for (int aantalSpelers = 0; aantalSpelers < playersExpected; aantalSpelers++) {
            spelerNummer = aantalSpelers + 1;
            players[aantalSpelers] = new Player();
            System.out.println("Welke kleur voor speler " + spelerNummer + "?");
            players[aantalSpelers].makePlayer();
            players[aantalSpelers].playerNumber = spelerNummer;
            System.out.println("Nieuwe speler " + (spelerNummer) + " met de kleur " + players[aantalSpelers].colour);
            System.out.println();
        }
        System.out.println("Gooi met G en sluit het spel af met X.");
        System.out.println();
    }
}

class Dice {
    int lastThrow;

    public int diceThrow() {
        Random rand = new Random();
        int randomNum = rand.nextInt(6) + 1;
        lastThrow = randomNum;
        return randomNum;
    }
}

class Player {
    int originalPosition;
    boolean lastplace = false;
    boolean opgesloten = false;
    Scanner input = new Scanner(System.in);
    String colour;
    int playerNumber = 0;
    int position = 0;
    boolean winner = false;
    boolean firstTurn = true;
    boolean skipTurn = false;

    public String makePlayer() {
        return colour = input.nextLine();
    }
}

class Board {
    Vakjes[] vakjes = new Vakjes[64];

    Board() {
        System.out.println("Preview van het bord.🔢");
        System.out.println();
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 0 -> vakjes[i] = new Start();
                case 6 -> vakjes[i] = new Brug();
                case 19 -> vakjes[i] = new Herberg();
                case 31 -> vakjes[i] = new Put();
                case 42 -> vakjes[i] = new Doornstruik();
                case 52 -> vakjes[i] = new Gevangenis();
                case 58 -> vakjes[i] = new Dood();
                case 63 -> vakjes[i] = new Finish();
                case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59 -> vakjes[i] = new Gans();
                default -> vakjes[i] = new Vakjes();
            }
            switch (i) {
                case 10, 20, 30, 40, 50, 60 -> {
                    System.out.println();
                    System.out.print("[" + vakjes[i].icoon + "]");
                }
                default -> System.out.print("[" + vakjes[i].icoon + "]");
            }
        }
        System.out.println();
        System.out.println();
    }

    void renderBoard() {
        System.out.println();
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 10, 20, 30, 40, 50, 60 -> {
                    System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT);
                    System.out.print("[" + vakjes[i].icoon + "]"+ConsoleColors.RESET);
                }
                default -> System.out.print(ConsoleColors.WHITE_BOLD_BRIGHT+"[" + vakjes[i].icoon + "]"+ConsoleColors.RESET);
            }

        }
        System.out.println();
        System.out.println(ConsoleColors.RESET);
    }
}

class Vakjes {
    char icoon = ' ';
    char trueIcoon = icoon;
    boolean bezet;

    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else {
            System.out.println(ConsoleColors.GREEN+"Dit is een leeg vakje.✔"+ConsoleColors.RESET);
        }

    }

    void uitvoeren(Player p, Dice dice1, Dice dice2, Vakjes vakjes) {
    }
    //void uitvoeren(Player p, Dice dice1, Dice dice2){}
}

class Brug extends Vakjes {
    {
        icoon = '=';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {

        p.position = 12;
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else
            System.out.println("U staat op een Brug ╰(*°▽°*)╯ , ga door naar vakje 12!");
    }
}

class Herberg extends Vakjes {
    {
        icoon = '^';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else {
            System.out.println("U staat op een herberg. Beurtje overslaan. 🛏");
            p.skipTurn = true;
        }
    }
}

class Put extends Vakjes {
    public Put() {
        int id = 31;
        icoon = 'U';
        char trueIcoon = icoon;

    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else {
            System.out.println("In de put! Balen. 🗑");
            if (!p.lastplace) {
                p.opgesloten = true;
                System.out.println(p.colour + " zit vast totdat iemand langsganst!");
            } else {
                p.skipTurn = true;
                System.out.println("Speler " + p.colour + " is de laatste, beurt overslaan!");
            }
        }
    }
}

class Doornstruik extends Vakjes {
    public Doornstruik() {
        icoon = '*';
        char trueIcoon = icoon;
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else
            System.out.println("Ai, in de doornstruik! 🛤 " + p.colour + " dwaalt terug naar vakje 37...");
        p.position = 37;
    }
}

class Gevangenis extends Vakjes {
    public Gevangenis() {
        icoon = '#';
        int id = 52;
        char trueIcoon = icoon;
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else {
            System.out.println("In de gevangenis 👮‍ met speler " + p.colour + " de jeugd van tegenwoordig ook...");
            if (!p.lastplace) {
                p.opgesloten = true;
                System.out.println(p.colour + " zit opgesloten totdat iemand langsganst!");
            } else {
                p.skipTurn = true;
                System.out.println("Speler " + p.colour + " is de laatste, beurt overslaan!");
            }
        }
    }
}

class Dood extends Vakjes {
    public Dood() {
        icoon = 'T';
        char trueIcoon = icoon;
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        System.out.println(ConsoleColors.BLACK_BACKGROUND_BRIGHT+ConsoleColors.RED_BRIGHT+"Speler " + p.colour + " is hartstikke dood 💀 , gelukkig respawn je gewoon bij het begin. Hoop dat je 9 kan gooien!"+ConsoleColors.RESET);
        p.position = 0;
    }
}

class Finish extends Vakjes {
    public Finish() {
        icoon = 'F';
        char trueIcoon = icoon;
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT+"Hoera, speler " + p.colour + " heeft gewonnen! Tot de volgende keer in deze vreselijke RNG machine."+ConsoleColors.RESET);
        System.exit(0);
    }
}

class Gans extends Vakjes {
    int totaalDice;

    public Gans() {
        icoon = 'G';
        char trueIcoon = icoon;
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2, Vakjes vakjes) {
        System.out.print(ConsoleColors.PURPLE_BOLD+"\"Gak!\" 🦆 zegt de gans terwijl het je een extra " + (dice1.lastThrow + dice2.lastThrow) + " stappen verplaatst! \n"+ConsoleColors.RESET);
        totaalDice = (dice1.lastThrow + dice2.lastThrow);
        if (p.position + totaalDice <= 63) {
            p.position += totaalDice;
            //System.out.println("Debug: IF <63 is uitgevoerd.");
            switch (p.position) {
                case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59 -> {
                    this.uitvoeren(p, dice1, dice2, vakjes);
                    if (vakjes.bezet)
                        p.position = p.originalPosition;
                }
            }
            System.out.println("Speler " + p.colour + " staat op vakje " + p.position + ".");

        } else {
            totaalDice = (63 - (p.position + totaalDice));
            p.position = 63 + totaalDice;
            //System.out.println("DEBUG: Else is uitgevoerd.");
            switch (p.position) {
                case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59 -> {
                    this.uitvoeren(p, dice1, dice2, vakjes);
                    if (vakjes.bezet)
                        p.position = p.originalPosition;
                }
            }
            System.out.println("Speler " + p.colour + " staat op vakje " + p.position + ".");
        }
        //p.position = p.position+dice1.lastThrow+dice2.lastThrow;
    }
}

class Start extends Vakjes {
    Start() {
        icoon = 'S';
        trueIcoon = icoon;
    }
}
class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE
}