# KPGR1 

## Controls

* **Mouse (left click and drag)** draws a line
* **Shift (hold while dragging)** draws either horizontal, vertical or diagonal line
* **L** switches back to line mode
* **P** switches to polygon mode. Every click creates new vertex of polygon
* **Enter** closes and saves currently drawn polygon to scene
* **F** switches to fill mode and opens dialog to choose filling algorithm
    * **Seed Fill (BACKGROUND_LIMITED)** click to start (stops when no more points with BACKGROUND_COLOR are available)
    * **Seed Fill (BORDER_LIMITED)** click to start (stops when reaching WHITE border)
    * **Scan-line** filling applied to the last completed polygon
* **X** displays clipping boundaries and opens the dialog to choose from clipping methods
  * **Cohen-Sutherland** for lines (rectangle)
  * **Sutherland-Hodgeman** for polygons (pentagon)
* **C** clears the canvas and resets all data structures and states

## Implementation Notes

* **loss of color** when Sutherland-Hodgeman applied to polygons filled by seed fill method, the filling does not persist