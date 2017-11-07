# Minesweeper

The Minesweeper module adds minefields. The Basic goal is to mine all the surrounding blocks and mark all the mines without setting off the entire field. 

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
