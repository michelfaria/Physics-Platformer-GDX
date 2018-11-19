#!/usr/bin/python

import os
import sys
import glob
from PIL import Image

print("Enter glob expression: ");
expr = input()

imgs = glob.glob(expr)

print("Resizing the following images: " + str(imgs))
print("Enter Y to continue; any other key to abort.")
inp = input()

if inp.lower() != "y":
	print("Abort")
	exit(0)
	
print("Input new resolution (%nx%n):")
res = input().split("x", 1)

if len(res) != 2:
	print("Invalid input, resolved to: " + str(res));
	exit(0)
	
width, height = int(res[0]), int(res[1])
	
for img in imgs:
	if os.path.isfile(img):
		img_b = Image.open(img)
		resized = img_b.resize((width, height), Image.ANTIALIAS)
		resized.save(img)