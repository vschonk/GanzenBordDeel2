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
                players[player].originalPosition = players[player].position;
                System.out.println("Test Speler " + players[player].playerNumber + " (" + players[player].colour + ")" + " op positie " + players[player].position + " is aan de beurt!");
                System.out.println("Gooi met G en stop programma met X.");
                try {
                    inputChar = input.next().charAt(0);
                    if (inputChar == 'G') {
                        totalDice = dice1.diceThrow() + dice2.diceThrow();
                        System.out.println("Uw worp is " + dice1.lastThrow + " " + dice2.lastThrow + " dus totaal: " + totalDice + " DEBUG:Het verwachte vakje is " + (players[player].position + totalDice));
                        if (players[player].position + totalDice <= 63) {
                            players[player].position += totalDice;
                            System.out.println("Debug: IF <=63 is uitgevoerd.");
                        } else {
                            totalDice = (63 - (players[player].position + totalDice));
                            players[0].position = 63 + totalDice;

                            System.out.println("DEBUG: Else is uitgevoerd.");
                        }
                        System.out.println("Speler " + players[player].colour + " staat op vakje " + players[player].position + ".");
                        switch (players[player].position){
                            case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59:
                                board.vakjes[players[player].position].uitvoeren(players[player], dice1, dice2, board.vakjes[player]);
                            default:
                                board.vakjes[players[player].position].uitvoeren(players[player], dice1, dice2);

                        }
                        board.vakjes[players[player].originalPosition].bezet = false;
                        board.vakjes[players[player].position].bezet = true;
                        }
                        board.renderBoard(players[player]);
                    if (inputChar == 'X') {
                        System.out.println("RAGEQUIT.");
                        System.exit(1337);
                    }
                } catch (Exception e) {
                    System.out.println("Alleen G of X doen iets, nerd.");
                }


            }
            //board.vakjes[players[0].position].uitvoeren(players[0]);
        }
    }


    Spel() {
        System.out.println("Hoeveel Spelers?");
        boolean correctSpelers = false;
        int playersExpected = input.nextInt();
        do {
            if (playersExpected > 6) {
                System.out.println("Mag alleen lager dan 7, pannenkoek. Vul dat maar es in.");
                playersExpected = input.nextInt();

            }
            if (playersExpected < 0) {
                System.out.println("Grapjas.");
                playersExpected = input.nextInt();
            } else correctSpelers = true;
        } while (!correctSpelers);
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
    Scanner input = new Scanner(System.in);
    String colour;
    int playerNumber;
    int position = 0;
    boolean winner = false;
    boolean skipTurn = false;

    public String makePlayer() {
        colour = input.nextLine();
        return colour;
    }
}

class Board {
    Vakjes[] vakjes = new Vakjes[64];

    Board() {
        System.out.println("Preview van het bord.");
        System.out.println();
        for (int i = 0; i < 64; i++) {
            switch (i) {
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

    void renderBoard(Player p) {
        System.out.println();
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 10, 20, 30, 40, 50, 60:
                    System.out.println();
                    if (i == p.position)
                        System.out.print("[" + p.playerNumber + "]");
                    else
                        System.out.print("[" + vakjes[i].icoon + "]");
                    break;
                default:
                    if (i == p.position)
                        System.out.print("[" + p.playerNumber + "]");
                    else
                        System.out.print("[" + vakjes[i].icoon + "]");
            }

        }
        System.out.println();
    }
}

class Vakjes {
    protected char icoon = ' ';
    byte id;
    boolean bezet;

    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else {
            System.out.println("Dit is een leeg vakje.");
        }

    }
    void uitvoeren(Player p, Dice dice1, Dice dice2, Vakjes vakjes){}
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
            System.out.println("U staat op een Brug, ga door naar vakje 12!");
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
        } else
            System.out.println("U staat op een herberg. Beurtje overslaan.");
    }
}

class Put extends Vakjes {
    public Put() {
        icoon = 'U';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else
            System.out.println("Oei, " + p.colour + " is in de put gevallen! Nu moet je wachten tot iemand je passeert. Als je de laatste bent moet je een beurt overslaan!");
    }
}

class Doornstruik extends Vakjes {
    public Doornstruik() {
        icoon = '*';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else
            System.out.println("Ai, in de doornstruik! " + p.colour + " dwaalt terug naar vakje 37...");
        p.position = 37;
    }
}

class Gevangenis extends Vakjes {
    public Gevangenis() {
        icoon = '#';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        if (bezet) {
            System.out.println("Vakje is bezet, jammer!");
            p.position = p.originalPosition;
            System.out.println("Terug naar vakje " + p.originalPosition + ".");
        } else
            System.out.println("In de gevangenis met speler " + p.colour + " de jeugd van tegenwoordig ook... Wachten tot iemand langskomt om je te bevrijden of als je de laatste bent, een beurt overslaan.");
    }
}

class Dood extends Vakjes {
    public Dood() {
        icoon = 'T';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        System.out.println("Speler " + p.colour + " is hartstikke dood, gelukkig respawn je gewoon bij het begin. Hoop dat je 9 kan gooien!");
        p.position = 0;
    }
}

class Finish extends Vakjes {
    public Finish() {
        icoon = 'F';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2) {
        System.out.println("Hoera, speler " + p.colour + " heeft gewonnen! Tot de volgende keer in deze vreselijke RNG machine.");
        System.exit(0);
    }
}

class Gans extends Vakjes {
    int totaalDice;

    public Gans() {
        icoon = 'G';
    }

    @Override
    void uitvoeren(Player p, Dice dice1, Dice dice2, Vakjes vakjes) {
        System.out.print("\"Gak!\" zegt de gans terwijl het je een extra " + (dice1.lastThrow + dice2.lastThrow) + " stappen verplaatst! \n");
        totaalDice = (dice1.lastThrow + dice2.lastThrow);
        if (p.position + totaalDice <= 63) {
            p.position += totaalDice;
            System.out.println("Debug: IF <63 is uitgevoerd.");
            switch(p.position) {
                case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59:
                    this.uitvoeren(p, dice1, dice2);
                    if (vakjes.bezet)
                        p.position = p.originalPosition;
                    break;
            }
            System.out.println("Speler "+p.colour+" staat op vakje "+p.position+".");

        } else {
            totaalDice = (63 - (p.position + totaalDice));
            p.position = 63 + totaalDice;
            System.out.println("DEBUG: Else is uitgevoerd.");
            switch(p.position){
                case 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59:
                    this.uitvoeren(p,dice1,dice2);
                    if (vakjes.bezet=true)
                        p.position = p.originalPosition;
                    break;}
            System.out.println("Speler "+p.colour+" staat op vakje "+p.position+".");
        }
        //p.position = p.position+dice1.lastThrow+dice2.lastThrow;
    }
}