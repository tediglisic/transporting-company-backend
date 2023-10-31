
CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
   ON  [dbo].[Order] 
   AFTER  UPDATE
AS 
BEGIN

declare @cursor cursor
declare @profit decimal(10,3)
 declare @IdOrder int
 declare @datumStizanja Date

 set @cursor = cursor for
	select IdOrder, RecievingTime 
	from  [dbo].[Order]
	where RecievingTime is not NULL

 open @cursor

	fetch next from @cursor
	into @IdOrder, @datumStizanja

	while @@FETCH_STATUS=0
	begin

	declare @cursor1 cursor
	declare @sum int
	declare @IdShop int
	declare @IdBuyer int

	select @IdBuyer = IdBuyer
	from [dbo].[Order]
	where IdOrder = @IdOrder


	set @cursor1 = cursor for
		select sum(oa.PricePaid), a.IdShop 
		from  Order_Article oa join Article a on oa.IdArticle = a.IdArticle
		where oa.IdOrder = @IdOrder
		group by a.IdShop


	open @cursor1
	fetch next from @cursor1
	into @sum, @IdShop

	while @@FETCH_STATUS=0
	begin

	print(@sum)

	declare @IdTransaction int
	declare @pukoPara int

	select @pukoPara = sum(b.TotalPrice)
    from Transaction_Buyer b join [dbo].[Transaction] t on b.IdTransaction = t.IdTransaction
    where b.IdBuyer = @IdBuyer  and  t.ExecutionTime-30< t.ExecutionTime 

	if(@pukoPara >= 10000)
		begin
			insert into [dbo].[Transaction] (TotalPrice,ExecutionTime,IdOrder)
			values(@sum*0.97,@datumStizanja,@IdOrder)

			select @IdTransaction = IdTransaction
			from [dbo].[Transaction] where IdTransaction = SCOPE_IDENTITY() 

			insert into Transaction_Shop(IdTransaction,TotalPrice,IdShop)
			values(@IdTransaction,@sum*0.97,@IdShop)

			if(not exists ( select * from SystemTable))
			begin
				insert into SystemTable (Profit)
				values(@sum*0.03)
			end
			else
				begin
			update SystemTable
			set Profit = Profit + @sum*0.03
			end
			
		end
	else
		begin

			insert into [dbo].[Transaction] (TotalPrice,ExecutionTime,IdOrder)
			values(@sum*0.95,@datumStizanja,@IdOrder)

			select @IdTransaction = IdTransaction
			from [dbo].[Transaction] where IdTransaction = SCOPE_IDENTITY() 

			insert into Transaction_Shop(IdTransaction,TotalPrice,IdShop)
			values(@IdTransaction,@sum*0.95,@IdShop)

			if(not exists ( select * from SystemTable))
			begin
				insert into SystemTable (Profit)
				values(@sum*0.05)
			end
			else
				begin
				update SystemTable
				set Profit = Profit + @sum*0.05
			end

		end
	fetch next from @cursor1
	into @sum, @IdShop

	end

	close @cursor1

	fetch next from @cursor
	into @IdOrder, @datumStizanja

	end
	close @cursor
	
END
GO

drop trigger TR_TRANSFER_MONEY_TO_SHOPS



create procedure SP_FINAL_PRICE 
@orderId int,
@finalPrice decimal(10,3) output
as
begin
	declare @cursor cursor
	declare @discount int
	declare @price int

	set @finalPrice = 0

	set @cursor = cursor for
	select  IdArticle
	from Order_Article
	where IdOrder = @orderId

	open @cursor
	declare @id int

	fetch next from @cursor
	into @id

	while @@FETCH_STATUS = 0
	begin
		declare @num int

		select @num = oa.Number, @price = Price, @discount = Discount
		from Order_Article oa, Article a, Shop s
		where a.IdArticle = @id and oa.IdArticle=a.IdArticle and a.IdShop = s.IdShop

		
		set @finalPrice = @finalPrice + @num*@price*(1-@discount*1.0/100)
		
		fetch next from @cursor
		into @id
	end

	close @cursor
	return @finalPrice

end
go

declare @final decimal(10,3)
exec SP_FINAL_PRICE 1,  @final output

select @final

select *
from Order_Article

select *
from Article

drop proc SP_FINAL_PRICE
