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
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ArticleOperations;


/**
 *
 * @author glisha
 */
public class gt190572_ArticleOperations implements ArticleOperations {

    
    @Override
    public int createArticle(int i, String string, int i1) {
        
         Connection con = DB.getInstance().getConnection();
         //Proverava postajanje prodavnice sa Id-jem
         if(!Check.checkShop(i) || i1<=0) return -1;
         
         
         //Proverava da li artikal vec postoji u prodavnici
          try(PreparedStatement ps = con.prepareStatement("select* from Article where IdShop=? and Name=?");){
              ps.setInt(1,i);
              ps.setString(2, string);
             
             ResultSet rs1 = ps.executeQuery();
             if(rs1.next()) return -1;
             
             rs1.close();
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
          
          //Ubacuje novi artikal sa odredjenim id-jem 
         try(PreparedStatement ps1 = con.prepareStatement("insert into Article (Name,Counter,IdShop,Price) values (?,?,?,?)"
                 + " select IdArticle from Article where IdArticle = SCOPE_IDENTITY()");){
             
              ps1.setString(1, string);
              ps1.setInt(2,0 );
              ps1.setInt(3, i);
              ps1.setInt(4,i1);
              
              ResultSet rs =ps1.executeQuery();
              if(!rs.next()) return -1;
              return rs.getInt(1);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
    
}

