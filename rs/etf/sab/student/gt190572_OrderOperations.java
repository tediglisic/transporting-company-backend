/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.OrderOperations;
import sun.tools.java.Type;

/**
 *
 * @author glisha
 */
public class gt190572_OrderOperations implements OrderOperations{

    
    private boolean profit = false;
    
    private boolean checkSystemDiscount(int IdBuyer){
        
        //Dohvatam danasnji datum i datum od pre 30 dana
        gt190572_GeneralOperations g = new gt190572_GeneralOperations();
        Calendar today = g.getCurrentTime();
        today.add(Calendar.DATE, -30);
        Calendar daysAgo = (Calendar) today.clone();
        
        today = g.getCurrentTime();
        
        
        //Dohvatam sumu koji je korisnik platio u prethodnih 30 dana
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("select sum(b.TotalPrice)"
                + " from Transaction_Buyer b join [dbo].[Transaction] t on b.IdTransaction = t.IdTransaction"
                + " where b.IdBuyer = ? and  ?< t.ExecutionTime ")){
            
            ps.setInt(1, IdBuyer);
            ps.setDate(2,  new java.sql.Date(daysAgo.getTimeInMillis()));
            
            ResultSet rs = ps.executeQuery();
            rs.next();
            
            if(rs.getInt(1)>=10000) return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    
    @Override
    public int addArticle(int i, int i1, int i2) {
        
       //Proverava postojanje artikla u prodavnici i narudzbine kupca, kao i kolicinu koja bi da se doda
       if(!Check.checkArticle(i1)) return -1;
       if(!Check.checkOrder(i)) return -1;
       if(i2 < 0) return -1;
       
       
       int price = 0;
       
       
       Connection con = DB.getInstance().getConnection();
         
        //Provera da li u narudzbini vec postoji artikal u narudzbini i da li ima dovoljnno da se doda
         try(PreparedStatement cs = con.prepareStatement("select o.Number from Order_Article o join Article a "
                 + "on o.IdArticle = a.IdArticle where o.IdOrder = ? and o.IdArticle = ? and a.Counter>=?")){
              
                cs.setInt(1, i);
                cs.setInt(2, i1);
                cs.setInt(3, i2);
              
              ResultSet rs = cs.executeQuery();
              
              //U slucaju da svi prvi navedeni uslovi postoje dodaj 
              if(rs.next()){
                  
                  //Dodaj kolicinu artikla
                  try(PreparedStatement ps = con.prepareStatement("update Order_Article set Number = Number + ? "
                          + "where IdOrder = ? and IdArticle = ?");){
                  
                        ps.setInt(1, i2);
                        ps.setInt(2, i); 
                        ps.setInt(3,i1);
                        
                        ps.executeUpdate();
                                
  
                  }catch (SQLException ex) {
                      Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  
                  //Oduzmi iz prodavnice novu kolicinu artikla
                  try(PreparedStatement ps1 = con.prepareStatement("update Article  set Counter = Counter-? where IdArticle = ?");){
                  
                                        ps1.setInt(2, i1);
                                        ps1.setInt(1, i2); 
                        
                                        ps1.executeUpdate();
                                
                  }   catch (SQLException ex) {
                      Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);              
                  }
                  
              }else{
                  
                  //Ukoliko nije artikal dodat u porudzbinu, proveriti da li postoji odredjeni artikal na stanju 
                  try(PreparedStatement ps = con.prepareStatement("select * from Article where IdArticle = ? and Counter>=?");){
                  
                      
                        ps.setInt(1, i1); 
                        ps.setInt(2,i2);
                        
                        ResultSet rs1 = ps.executeQuery();
                        
                        //Ukoliko postoji odraditi sve neophodne update-e
                        if(rs1.next()){
                              try(PreparedStatement ps1 = con.prepareStatement("insert into Order_Article (IdArticle,IdOrder,Number,PricePaid)"
                                   + "values (?,?,?,?)");){
                  
                                       ps1.setInt(1, i1);
                                       ps1.setInt(2, i); 
                                       ps1.setInt(3, i2);
                                       ps1.setInt(4, 0); 
                        
                                       ps1.executeUpdate();
                              }catch (SQLException ex) {
                                         Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                              }
                                    
                              try(PreparedStatement ps1 = con.prepareStatement("update Article  set Counter = Counter-? where IdArticle = ?");){
                  
                                        ps1.setInt(2, i1);
                                        ps1.setInt(1, i2); 
                        
                                        ps1.executeUpdate();
                                
                              }catch (SQLException ex) {
                                         Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                              }
                        }
                  
                }catch (SQLException ex) {
                      Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
             }
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         
        return 1;
    }

    @Override
    public int removeArticle(int i, int i1) {
        
        
        //Provera postojanja datog artikla i date porudzbine
        if(!Check.checkArticle(i1)) return -1;
        if(!Check.checkOrder(i)) return -1;
        
        //Kolicina koja se vraca nazad u prodavnicu
        int count = 0;
       
       //Uzima se item u kom se nalazi zadati artikal i porudzbina
       Connection con = DB.getInstance().getConnection();
       try(PreparedStatement ps2 = con.prepareStatement("Select Number from Order_Article where IdArticle = ? and IdOrder = ?")){
            ps2.setInt(1,i1);
            ps2.setInt(2,i);
            ResultSet rs = ps2.executeQuery();
           
            if(rs.next()) count = rs.getInt(1);
           
       } catch (SQLException ex) {
            Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       //Vrati nazad u prodavnicu zadati artikal iz porudzbine
       try(PreparedStatement cs = con.prepareStatement("DELETE Order_Article where IdArticle = ? and IdOrder = ? "
                 + "update Article set Counter = Counter + ?")){
              
              cs.setInt(1, i1);
              cs.setInt(2, i);
              cs.setInt(3, count);
             
        
              cs.executeUpdate();
          
        } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getItems(int i) {
        
        //Proverava postojanje zadate porudzbine
        if(!Check.checkOrder(i)) return null;
        
        List<Integer> items = null;
        
        //Dohvata sve item koji u sebi imaju porudzbinu
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("select IdOrderArticle from Order_Article where IdOrder = ?")){ 
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
              
            while(rs.next()){
                if(items == null) items = new ArrayList<>();
                items.add(rs.getInt(1));
            }
            return items;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
         }   
        
        return null;
    }

    @Override
    public int completeOrder(int i) {
       
        //Proverava da li porudzbina postoji
        if(!Check.checkOrder(i)) return -1;
        
        int buyer = 0;
        int IdCityBuyer = 0;
        
        Connection con = DB.getInstance().getConnection();
        
        java.sql.Date d = null;
        
        //Uzimam danasnji datum
        try(PreparedStatement ps = con.prepareStatement("select Date from TodayDate")){
            
            ResultSet rs = ps.executeQuery();
            rs.next();
            d = rs.getDate(1);
            
        } catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        //Status porudzbine menjam u sent ako je prethodni status bio created
        try(PreparedStatement ps = con.prepareStatement("update [dbo].[Order] set Status = 'sent', SendingTime = ? where IdOrder = ? and Status = 'created' "
                + " select IdBuyer from [dbo].[Order] where IdOrder=?")){
            ps.setInt(2,i);
            ps.setDate(1, d);
            ps.setInt(3,i); 
             
            ResultSet rs = ps.executeQuery();
            
            if(!rs.next()) return -1;
            
            //Uzima kupca porudzbine
            buyer = rs.getInt(1);
            rs.close();
            
        } catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //Update pricePaid in OrderArticle - za svaki artikal postavlja cenu koja se placa sa popustom
        try(PreparedStatement ps1 = con.prepareStatement("select IdOrderArticle from Order_Article where IdOrder = ?")){
            ps1.setInt(1,i);
            ResultSet rs1 = ps1.executeQuery();
            
            while(rs1.next()){
                
                //Dohvata izracunatu cenu proizvoda u porudzbini 
                try(PreparedStatement ps2 = con.prepareStatement("select oa.Number*a.Price*(1-s.Discount*0.01)"
                        + "from Order_Article oa join Article a on oa.IdArticle = a.IdArticle "
                        + "join Shop s on a.IdShop = s.IdShop"
                        + " where IdOrderArticle = ?")){
                    
                    ps2.setInt(1,rs1.getInt(1));
                    ResultSet rs2 = ps2.executeQuery();
                    rs2.next();
                    
                    double pricePaid = rs2.getDouble(1);
                    
                    //Update svaki artikal posebno
                    try(PreparedStatement ps3 = con.prepareStatement("update Order_Article set PricePaid = ? where IdOrderArticle =?")){
                    
                        ps3.setDouble(1, pricePaid);
                        ps3.setInt(2, rs1.getInt(1));
                     
                        ps3.executeUpdate();
                    
                    }catch (SQLException ex) {
                        Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }catch (SQLException ex) {
                    Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
       
            
        } catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //Proverava da li buyer ima dodatni popust na celu porudzbinu od strane sistema
        checkSystemDiscount(buyer);
        
        //Dohvata ukupnu cenu svoje porudzbine
        BigDecimal finalPrice  = getFinalPrice(i);
        
        //Id transakcije kupca kako bi moglo u posebnu tabelu da se postavi
        int idTrans = 0;
        
        try(PreparedStatement ps1 = con.prepareStatement("insert into [dbo].[Transaction] (TotalPrice,ExecutionTime,IdOrder) "
                + "values(?,?,?)"
                + "select IdTransaction from[dbo].[Transaction] where IdTransaction = SCOPE_IDENTITY()"
            )){
          
            
                try(PreparedStatement ps2 = con.prepareStatement("select Date from TodayDate")){
                
                    ResultSet rs5 = ps2.executeQuery();
                    rs5.next();
                
                    ps1.setDouble(1, finalPrice.doubleValue());
                    ps1.setDate(2, rs5.getDate(1));
                    ps1.setInt(3,i);
            
                    ResultSet rs4 = ps1.executeQuery();
                    if(rs4.next()){
                        idTrans = rs4.getInt(1);
                    }
                
                }
            
        }catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Ubacuje u posebnu tabelu transakcije za kupca
        try(PreparedStatement ps2 = con.prepareStatement("insert into Transaction_Buyer (IdTransaction,TotalPrice,IdBuyer) "
                + "values(?,?,?)")){
           
              ps2.setInt(1, idTrans);
              ps2.setDouble(2,finalPrice.doubleValue());
              ps2.setInt(3, buyer);
              
              ps2.executeUpdate();
            
        } catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        int numCity = 0;
         
         //BROJ GRADOVA
        try(PreparedStatement ps2 = con.prepareStatement("select count(*) from city")){
           
              ResultSet rs = ps2.executeQuery();
              if(rs.next()) numCity = rs.getInt(1);
            
        } catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
          
         //IdCityBuyer
        try(PreparedStatement ps2 = con.prepareStatement("select IdCity from buyer where IdBuyer = ?")){
           
            ps2.setInt(1,buyer);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()) IdCityBuyer = rs.getInt(1);
            
        } catch (SQLException ex) {
             Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Pravi TracingOrder tabelu za datu sent porudzbinu
        try(PreparedStatement s1 = con.prepareStatement("select distinct(s.IdCity) "
                + "from Order_Article oa join Article a on oa.IdArticle = a.IdArticle join Shop s on a.IdShop = s.IdShop "
                + " where oa.IdOrder = ? ")){
            
            s1.setInt(1, i);
            ResultSet rs = s1.executeQuery();
            ArrayList<Integer> cities = new ArrayList<>();
            
            while(rs.next()){
                cities.add(rs.getInt(1));
            }
            
            Rastojanje r = new Rastojanje(numCity);
            r.OdrediRastojanjeSvega(IdCityBuyer,i,cities);
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
        return 1;
    }

    @Override
    public BigDecimal getFinalPrice(int i) {
        Connection con = DB.getInstance().getConnection();
         try(CallableStatement cs = con.prepareCall("{call SP_FINAL_PRICE(?,?)}")){
              cs.setInt(1, i); 
              cs.registerOutParameter(2, Types.DOUBLE);
              cs.execute();
             
              if(profit) return  cs.getBigDecimal(2).multiply(new BigDecimal("0.98")).setScale(3);
              
              return cs.getBigDecimal(2).setScale(3);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(-1).setScale(3);
    }

    
    private double AddDiscount(int idArticle){
        
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("select Price , Discount from Article a join Shop s "
                + "on a.IdShop = s.IdShop where IdArticle = ?");){
            
            ps.setInt(1,idArticle);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                return rs.getInt(1)*rs.getInt(2)*(1.0/100);
            }
         
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        return 0;
        
    }
    
    @Override
    public BigDecimal getDiscountSum(int i) {
      
        if(!Check.checkOrder(i)) return new BigDecimal(-1);
        
        double finalPrice = 0;
        double discount = 0;
        double price = 0;
        
        
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("select oa.IdArticle, a.Price , oa.Number from  Article a join Order_Article oa  on a.IdArticle = oa.IDArticle "
                + " join [dbo].[Order] o on oa.IdOrder = o.IdOrder "
                + " where oa.IdOrder = ? and o.Status != 'completed'");){
            
            ps.setInt(1,i);
            
            ResultSet rs = ps.executeQuery();
            
            if(!rs.next()) return new BigDecimal(-1).setScale(3);
            
             price += rs.getDouble(2)*rs.getInt(3);
             discount+=this.AddDiscount(rs.getInt(1))*rs.getInt(3);
            
            while(rs.next()){
                
               price += rs.getDouble(2)*rs.getInt(3);
              
                
            }
            
            discount = this.getFinalPrice(i).doubleValue();
            
            System.out.println("Price: "+ price + ", Discount: "+discount);
            if(profit) {
            return new BigDecimal(price-discount*0.98).setScale(3);
            }
            return new BigDecimal(price-discount).setScale(3);
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        
            return new BigDecimal(-1).setScale(3);
        
       
    }

    @Override
    public String getState(int i) {
        
        //Vraca status zadate porudzbine
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement cs = con.prepareStatement("select Status from [dbo].[Order] where IdOrder = ?")){
         
              cs.setInt(1, i);
              ResultSet rs = cs.executeQuery();
              
              if(rs.next() ){ 
                return rs.getString(1);
              }
         }
          
        catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Calendar getSentTime(int i) {
        Connection con = DB.getInstance().getConnection();
         
        try(PreparedStatement cs = con.prepareStatement("select SendingTime from [dbo].[Order] where IdOrder = ?")){
         
              cs.setInt(1, i);
              ResultSet rs = cs.executeQuery();
              
              if(rs.next() && rs.getDate(1)!=null){
                  Calendar c = Calendar.getInstance();
                  c.setTime(rs.getDate(1));
                  return c; 
              }
         }   
        catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Calendar getRecievedTime(int i) {
        
        
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement cs = con.prepareStatement("select RecievingTime from [dbo].[Order] where IdOrder = ?")){
         
              cs.setInt(1, i);
              ResultSet rs = cs.executeQuery();
              
              if(rs.next() && rs.getDate(1)!=null){
                  Calendar c = Calendar.getInstance();
                  c.setTime(rs.getDate(1));
                  
                  return c;
              }
         }
              
              
        catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int getBuyer(int i) {
        
        Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select IdBuyer from [dbo].[Order] where IdOrder = ?")){
             
            cs.setInt(1, i);
              
            ResultSet rs =  cs.executeQuery();
            if(rs.next()){
                int i1 = rs.getInt(1);
                rs.close();
                return i1;
            }
            rs.close();
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
         }
        return -1;
    }

    @Override
    public int getLocation(int i) {
        
        Date t = null;
        Connection con = DB.getInstance().getConnection();
        
        
        //Uzeti danasnji dan
        try(Statement s = con.createStatement()){
            
            ResultSet rs = s.executeQuery("select Date from TodayDate");
            if(rs.next()){
                t = rs.getDate(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("rs.etf.sab.student.gt190572_OrderOperations.getLocation()");
        System.out.println(t);
        
        //Pogledati koji je prvi najveci datum pre danasnjeg dana i videti lokaciju paketa
        try(PreparedStatement cs = con.prepareStatement("select top(1) Location from TracingOrder where IdOrder = ? and Date <= ? "
                 + "order by Date desc ")){
             
              cs.setInt(1, i);
              cs.setDate(2, t);
              
            ResultSet rs =  cs.executeQuery();
            if(rs.next()){
                int i1 = rs.getInt(1);
                rs.close();
                return i1;
            }
            rs.close();
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
}
