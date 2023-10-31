
CREATE TABLE [Article]
( 
	[IdArticle]          integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Name]               varchar(100)  NOT NULL ,
	[Counter]            integer  NOT NULL ,
	[IdShop]             integer  NOT NULL ,
	[Price]              integer  NOT NULL 
)
go

CREATE TABLE [Buyer]
( 
	[IdBuyer]            integer  IDENTITY ( 1,1 )  NOT NULL ,
	[IdCity]             integer  NOT NULL ,
	[Name]               varchar(100)  NOT NULL ,
	[Credits]            decimal(10,3)  NULL 
)
go

CREATE TABLE [City]
( 
	[IdCity]             integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Name]               varchar(100)  NOT NULL 
)
go

CREATE TABLE [CityConnection]
( 
	[IdCity2]            integer  NOT NULL ,
	[IdCity1]            integer  NOT NULL ,
	[Distance]           integer  NOT NULL ,
	[IdLine]             integer  IDENTITY ( 1,1 )  NOT NULL 
)
go

CREATE TABLE [Order]
( 
	[IdOrder]            integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Status]             varchar(20)  NOT NULL ,
	[IdBuyer]            integer  NOT NULL ,
	[SendingTime]        datetime  NULL ,
	[RecievingTime]      datetime  NULL 
)
go

CREATE TABLE [Order_Article]
( 
	[IdArticle]          integer  NOT NULL ,
	[IdOrder]            integer  NOT NULL ,
	[Number]             integer  NOT NULL ,
	[IdOrderArticle]     integer IDENTITY ( 1,1 ) NOT NULL ,
	[PricePaid]          integer  NULL 
)
go

CREATE TABLE [Shop]
( 
	[IdShop]             integer  IDENTITY ( 1,1 )  NOT NULL ,
	[IdCity]             integer  NOT NULL ,
	[Discount]           integer  NULL ,
	[Name]               varchar(100)  NOT NULL 
)
go

CREATE TABLE [SystemTable]
( 
	[IdSystem]           integer IDENTITY ( 1,1 ) NOT NULL ,
	[Profit]             decimal(10,3)  NOT NULL 
)
go

CREATE TABLE [TodayDate]
( 
	[Date]               datetime  NULL ,
	[IdDate]             integer IDENTITY ( 1,1 ) NOT NULL 
)
go

CREATE TABLE [TracingOrder]
( 
	[IdTracing]          integer IDENTITY ( 1,1 ) NOT NULL ,
	[Date]               datetime  NOT NULL ,
	[Location]           integer  NOT NULL ,
	[IdOrder]            integer  NOT NULL 
)
go

CREATE TABLE [Transaction]
( 
	[IdTransaction]      integer IDENTITY ( 1,1 ) NOT NULL ,
	[TotalPrice]         decimal(10,3)  NOT NULL ,
	[ExecutionTime]      datetime  NOT NULL ,
	[IdOrder]            integer  NOT NULL 
)
go

CREATE TABLE [Transaction_Buyer]
( 
	[IdTransaction]      integer   NOT NULL ,
	[TotalPrice]         decimal(10,3)  NOT NULL ,
	[IdBuyer]            integer  NOT NULL 
)
go

CREATE TABLE [Transaction_Shop]
( 
	[TotalPrice]         decimal(10,3)  NULL ,
	[IdTransaction]      integer  NOT NULL ,
	[IdShop]             integer  NOT NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([IdArticle] ASC)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([IdBuyer] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdCity] ASC)
go

ALTER TABLE [CityConnection]
	ADD CONSTRAINT [XPKCityConnection] PRIMARY KEY  CLUSTERED ([IdLine] ASC)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([IdOrder] ASC)
go

ALTER TABLE [Order_Article]
	ADD CONSTRAINT [XPKOrder_Article] PRIMARY KEY  CLUSTERED ([IdOrderArticle] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([IdShop] ASC)
go

ALTER TABLE [SystemTable]
	ADD CONSTRAINT [XPKSystemTable] PRIMARY KEY  CLUSTERED ([IdSystem] ASC)
go

ALTER TABLE [TodayDate]
	ADD CONSTRAINT [XPKTodayDate] PRIMARY KEY  CLUSTERED ([IdDate] ASC)
go

ALTER TABLE [TracingOrder]
	ADD CONSTRAINT [XPKTracingOrder] PRIMARY KEY  CLUSTERED ([IdTracing] ASC)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([IdTransaction] ASC)
go

--ALTER TABLE [Transaction_Buyer]
	--ADD CONSTRAINT [XPKTransaction_Buyer] PRIMARY KEY  CLUSTERED ([IdTransaction] ASC)
--go

--ALTER TABLE [Transaction_Shop]
	--ADD CONSTRAINT [XPKTransaction_Shop] PRIMARY KEY  CLUSTERED ([IdTransaction] ASC)
--go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([IdShop])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [CityConnection]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdCity2]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CityConnection]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([IdCity1]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([IdBuyer])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Order_Article]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdArticle]) REFERENCES [Article]([IdArticle])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Order_Article]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [TracingOrder]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transaction_Buyer]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdTransaction]) REFERENCES [Transaction]([IdTransaction])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction_Buyer]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([IdBuyer])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Transaction_Shop]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdTransaction]) REFERENCES [Transaction]([IdTransaction])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction_Shop]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([IdShop])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go











