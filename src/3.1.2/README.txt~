BASIC NORMAL DUNGEON SIMULATOR

Steps:
1. Download the .zip file and un-archive it.
2. Open up Terminal, go to the directory, and type "javac *.java" (without the quotes). This compiles the Java programs.
3. I built a few teams and dungeons already. Try playing one of those first. Type one of the following into Terminal: "java game < game-adk-supers", "java game < game-bastet-ooh", "java game < game-horus-ooh".
4. If you want to build your own teams and dungeons, you can do one of the following:
	4.1. Type "java game", and the program will start asking for input. Look at one of the input files I made (such as "game-adk-supers") to get an idea of how the input works.
	4.2. You can also store your input into a file, and type "java game < [filename]" (replace [filename] with the name of the file).
	4.3. When you finish with your input, the program will output a "script" of everything you input. Look through the script to see if anything went wrong. (For example, it is easy to miss an "end" and mess up the input.) You can also copy and save the script if you want to use it again later.

Files:
	*.java, *.class: Java program files. You don't need to modify them.
	game-*: Input files that I created, that are ready for use. You should look at these files to familiarize yourself with the input.
	in*: Input files that I made for testing. Feel free to ignore them.
	team-*: The team portion of some input file. Feel free to ignore them.
	dungeon-*: The dungeon portion of some input file. Feel free to ignore them.

Notes:
1. Input
	1.1. The input format may be confusing at first, and I admit that it is not very user-friendly.
	1.2. Standard conventions for attributes: R=fire, B=water, G=wood, L=light, D=dark, H=heart.
	1.3. There are no standard conventions for types. As long as you are consistent throughout your input, you should be fine. Be aware that both Dragon and Devil start with D.
	1.2. For rainbow/combo leader skills, you input a list of options. When the program calculates leader skill multipler, it takes the highest multiplier out of all options that are satisfied. Unfortunately, some leaders have many options (Kushinada has 18, DQXQ has 6-choose-4 = 15), so input might be inconvenient here.
	1.3. For orb-changing skills that change more than one color, you provide two strings orb_change_from and orb_change_to. THE TWO STRINGS MUST BE OF EQUAL LENGTH. The first color in orb_change_from will be changed to the first color in orb_change_to, and same with the second colors, and so on. For example, for Ares' skill, orb_change_from = "BH" and orb_change_to = "RR".
2. Appearance
	2.1. Yes, the graphics are pretty primitive right now. I have no intention of including pictures, but I might make it look better in the future.
	2.2. Information about which team members attacks which enemy is shown in the Terminal, not on the game window.
3. Gameplay
	3.1. Only normal dungeons are currently available. No enemy skill mechanics have been implemented.
	3.2. Currently, dungeons have no randomness. That is, all monsters are pre-determined in the input, and their cooldowns are exactly their maximum cooldown (instead of cooldown +/- 1). I still need to figure out a clean way to determine which monsters to select at random on each floor.
	3.3. No awoken skills have been implemented yet. I will probably get to this next, so that this program can effectively simulate normal dungeons.
	3.4. Moving the orb with a mouse is probably harder than with a finger. If you want more orb movement time, then open up game.java and change the variable MOVEMENT_TIME (currently set at 4000) to however many milliseconds of time you want.
	3.5. Although possible, it is currently very difficult to do diagonal orb movement. I plan to reduce the precision required for diagonals.
	3.6. A lot of leader skills have not yet been implemented. Currently, only ATK (not HP or RCV) is modified by leader skills, so spike/rainbow/combo leader skills should work. (If your leader skills modify HP/RCV, then as a temporary fix, you can adjust each team member's HP and RCV directly in the input.)
	3.7. A lot of active skills have not yet been implemented. Currently, the only ones available are damage skills (i.e. ripper dragons), fixed-damage skills (i.e. Ra), gravity skills (i.e. Hera, Zeus), orb-changes, poison, and delay.

