# Minesweeper

The Minesweeper module generates clusters of mines. Each field is composed of mines and blocks that count it's neighbors. The goal is to flag the mines and avoid hitting a mine else the whole field will go off.

![Minesweeper](https://raw.githubusercontent.com/Terasology/Minesweeper/master/image.png)

## Rules

- Each block will count itself and all of it's neighbors. 
 - A Mine will count itself
- Each mine/counting block will count diagnols, sides and corners

## Controls
- E will mark a mine

## Notes
- Look for corner blocks 
- outside blocks of a field are never mines
- Rules from 2d minesweeper are still applicable 
