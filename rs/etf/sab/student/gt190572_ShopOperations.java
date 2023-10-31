/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ShopOperations;

/**
 *
 * @author glisha
 */
public class gt190572_ShopOperations implements ShopOperations{

    @Override
    public int createShop(String string, String string1) {
        if(Check.checkShopUnique(string)) return -1;
        if(!Check.checkCityUnique(string1)) return -1;
        
          Connection con = DB.getInstance().getConnection();
          try(PreparedStatement ps1 = con.prepareStatement("insert into Shop (IdCity,Discount,Name)\n" +
"					values(?,0,?)"
                 + " select IdShop from Shop where IdShop = SCOPE_IDENTITY()");
                  PreparedStatement st = con.prepareStatement("select IdCity from City where Name = ?");){
             
              st.setString(1,string1);
              ResultSet rs = st.executeQuery();
              rs.next();
              
              ps1.setInt(1,rs.getInt(1));
              ps1.setString(2, string);
              ResultSet rs1 = ps1.executeQuery();
              if(!rs1.next()) return -1;
             int id  = rs1.getInt(1);
              rs.close();
              rs1.close();
              return id;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setCity(int i, String string) {
        
        if(!Check.checkShop(i)) return -1;
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("update Shop\n" +
"					set IdCity = ?\n" +
"					where IdShop = ?");
                 PreparedStatement st = con.prepareStatement("select IdCity from City where Name = ?")){
             
              st.setString(1,string);
              ResultSet rs = st.executeQuery();
              rs.next();
              
              
              cs.setInt(1, rs.getInt(1));
              cs.setInt(2, i);
              
              cs.executeUpdate();
             
              return 1;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCity(int i) {
        
        if(!Check.checkShop(i)) return -1;
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select IdCity from Shop  where IdShop = ?")){
             
              cs.setInt(1, i);
              
              ResultSet rs = cs.executeQuery();
              rs.next();
              
              int id = rs.getInt(1);
              
              rs.close();
              return id;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setDiscount(int i, int i1) {
        
        if(!Check.checkShop(i)) return -1;
        if(i1 < 0) return -1;
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("update Shop\n" +
"			set Discount = ?\n" +
"			where IdShop = ?")){
             
             
              cs.setInt(2, i);
              cs.setInt(1, i1);
              
              cs.executeUpdate();
             
              return 1;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int increaseArticleCount(int i, int i1) {
        
        if(!Check.checkArticle(i)) return -1;
        if(i1 < 0) return -1;
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("update Article\n" +
"			set Counter = Counter + ?\n" +
"			where IdArticle = ?")){
              cs.setInt(2, i);
              cs.setInt(1, i1);
              
              cs.executeUpdate();
              
              
              try(PreparedStatement ps = con.prepareStatement("select Counter from Article where IdArticle = ?");){
                  
                  ps.setInt(1,i);
                  ResultSet rs = ps.executeQuery();
                  
                  rs.next();
                  
                  return rs.getInt(1);
              }
             
              
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getArticleCount(int i) {
        
        if(!Check.checkArticle(i)) return -1;
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select Counter from Article where IdArticle = ?")){
            
              cs.setInt(1, i);
              
              ResultSet rs = cs.executeQuery();
             rs.next();
             
              return rs.getInt(1);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getArticles(int i) {
        List<Integer> articles = null;
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("select IdArticle from Article where IdShop = ?")){ 
                stmt.setInt(1, i);
              ResultSet rs = stmt.executeQuery();
              while(rs.next()){
                  if(articles == null) articles = new ArrayList<>();
                  articles.add(rs.getInt("IdArticle"));
              }
              return articles;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public int getDiscount(int i) {
        if(!Check.checkShop(i)) return -1;
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select Discount from Shop where IdShop = ?")){
            
              cs.setInt(1, i);
              
              ResultSet rs = cs.executeQuery();
             rs.next();
              return rs.getInt(1);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
}
