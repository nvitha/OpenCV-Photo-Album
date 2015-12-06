create table Photo(
PhotoID int PRIMARY KEY NOT NULL, 
FileLocation varchar(255) NOT NULL);

create table ColorScheme(
ColorSchemeID int PRIMARY KEY NOT NULL, 
ColorSchemeName varchar(255) NOT NULL); 

create table ColorSchemeComponent(
ColorSchemeID int PRIMARY KEY NOT NULL, 
ColorSchemeComponentID int NOT NULL, 
ColorSchemeComponentName varchar(255) NOT NULL);

create table ColorStatistics(
PhotoID int PRIMARY KEY NOT NULL, 
ColorSchemeComponentID int NOT NULL, 
AverageValue int NOT NULL, 
STDEV int NOT NULL,
Check (AverageValue > 0 AND AverageValue < 361));

create table Shape(
ShapeID int PRIMARY KEY NOT NULL, 
ShapeName varchar(255) NOT NULL);

create table PhotoShape(
PhotoID int NOT NULL, 
ShapeID int NOT NULL, 
Loc1 int NOT NULL, 
Loc2 int NOT NULL); 

insert into ColorScheme
Values(1,RGB),
(2,HSV);

insert into ColorSchemeComponent
Values(1,1,Red),
(1,2,Green),
(1,3,Blue),
(2,4,Hue),
(2,5,Saturation),
(2,6,Value);

insert into Shape
Values(1,Line),
(2,Circle);
