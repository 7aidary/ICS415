# Bézier Curve Editor – Poly Support

This is a simple **visual Bézier curve editor** written in HTML5 + JavaScript. It allows users to **interactively control** Bézier curve points and supports **adding multiple connected Bézier curves** (poly Bézier).

## ✨ Features

- Drag and drop control points to reshape the curve.
- Add new connected Bézier curves with one click.
- Visual feedback for control points and connecting lines.
- Uses cubic Bézier curves (4 points per segment).
- Responsive and fast rendering using Canvas API.

## 📸 Demo

![screenshot](screenshot.png)  
*(Add your screenshot if desired)*

## 🧠 How It Works

- Each Bézier curve is defined by 4 control points.
- When you click “➕ Add New Curve”, it appends 3 new points to create a new connected Bézier segment.
- The curves are drawn by evaluating the cubic Bézier formula:
  B(t) = (1 - t)^3 * p0  + 3 * (1 - t)^2 * t * p1 + 3 * (1 - t) * t^2 * p2 + t^3 * p3


## 🔧 How to Use

1. Open `index.html` in any modern browser.
2. Drag the red control points to reshape the curve.
3. Click "➕ Add New Curve" to extend the path.


## 📁 Files

- `index.html` – main UI and logic
- `screenshot.png` – image of the editor in action

