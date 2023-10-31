/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author glisha
 */
public class Rastojanje {
    
    private int numNodes = 0;
    private ArrayList<Path> paths = new ArrayList<>();
    public boolean[] visited;
    
    
    public Rastojanje(int num){
        numNodes = num;
        visited = new boolean[num];
    }
    
    public class Path{
        
        public int price = 0;
        public int last = 0;
        public ArrayList<Integer> allCities = new ArrayList<>();
        
        public Path(){}
        
        public Path(Path p){
            price = p.price;
            for(int i = 0 ; i<p.allCities.size();i++)
            allCities.add(p.allCities.get(i));
        }
        
        
        public void incrementPrice(int inc){
            price+=inc;
        }
        
        public void setLast(int last){
            this.last = last;
            allCities.add(last);
        }
    }
    
    private void upDateBase(Path p, int IdOrder, int dodatniDani){
        
        int distance = 0;
        
        java.sql.Date d = new java.sql.Date(2022, 1,1 );
        Calendar c = Calendar.getInstance(); 
                
       Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("insert into TracingOrder (Date,Location,IdOrder) values"
                + "(?,?,?)")){
            
            for(int i = p.allCities.size()-1; i>0;i--){
            
                if(i == p.allCities.size()-1){
                    
                    try(PreparedStatement ps1 = con.prepareStatement("select Date from TodayDate")){
                        
                        ResultSet rs = ps1.executeQuery();
                        if(rs.next()){
                            d = rs.getDate(1);
                            c.setTime(d); 
                            ps.setDate(1, rs.getDate(1));
                            ps.setInt(2,p.allCities.get(i));
                            ps.setInt(3, IdOrder);
                            ps.executeUpdate();
                            
                            c.add(Calendar.DAY_OF_YEAR, dodatniDani);
                        }
                    }
                    
                }
                
                try(PreparedStatement ps1 = con.prepareStatement("select Distance from CityConnection "
                        + "where IdCity1=? AND IdCity2=? or IdCity1=? AND IdCity2=?")){
                        
                    ps1.setInt(1, p.allCities.get(i));
                    ps1.setInt(2, p.allCities.get(i-1));
                    ps1.setInt(3, p.allCities.get(i-1));
                    ps1.setInt(4, p.allCities.get(i));
                        
                    ResultSet rs = ps1.executeQuery();
                    if(!rs.next()) return;
                    distance = rs.getInt(1);
            
               }   catch (SQLException ex) {
                    Logger.getLogger(Rastojanje.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                    c.add(Calendar.DAY_OF_YEAR, distance);
               
                    ps.setDate(1, new java.sql.Date(c.getTimeInMillis()));
                    ps.setInt(2,p.allCities.get(i-1));
                    ps.setInt(3, IdOrder);
                    ps.executeUpdate();
                
            
        }
           
           
        
         }   catch (SQLException ex) {
            Logger.getLogger(Rastojanje.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void OdrediRastojanjeSvega(int start, int IdOrder, ArrayList <Integer> IdCitiesWithShop){
        
        Calendar calendar = Calendar.getInstance();
        
        Path fromBuyerToShop = shortesPathShop(start, IdOrder);
        int maxDana = -1;
        
        for(int i = 0; i < IdCitiesWithShop.size() ; i++){
            
            int p = shortestPathShopShop( fromBuyerToShop.last, IdCitiesWithShop.get(i));
            if(p > maxDana){
                maxDana = p;
            }
        }
        
        upDateBase(fromBuyerToShop, IdOrder, maxDana); 
    }
        
    
    public Path shortesPathShop(int start, int IdOrder){
        
        Path startNode = new Path();
        startNode.setLast(start);
        Connection con = DB.getInstance().getConnection();
        
       
        visited[start-1] = true;
        if (Check.checkCityHasShop(start)) return startNode;
         try(PreparedStatement ps1 = con.prepareStatement("select* from CityConnection where IdCity1=? or IdCity2=?")){
              
              ps1.setInt(1, start);
              ps1.setInt(2,start);
              ps1.execute();
              
              ResultSet rs = ps1.executeQuery();
              
              while(rs.next()){
                  Path newPath = new Path(startNode);
                  if(rs.getInt("IdCity1") == start){
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity2"));
                      paths.add(newPath);
                 
                  }else{
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity1"));
                      paths.add(newPath);
                   
                  }
                  
              }
              
              
              while(true){
                  int min = Integer.MAX_VALUE;
                  int index = -1;
                  Path minimum = new Path();
                  
                  for(int i=0;i<paths.size();i++){
                      Path p = paths.get(i);
                      if(p.price<min && visited[p.last-1]==false){
                          minimum = p;
                          min = p.price;
                          index = i;
                      }
                      
                  }
                  
                  
                  
                  paths.remove(index);
                  visited[minimum.last-1] = true;
                   if (Check.checkCityHasShop(minimum.last)) {
                      return minimum;
                  }
              
                  PreparedStatement ps2 = con.prepareStatement("select* from CityConnection where IdCity1=? or IdCity2=?");
                  ps1.setInt(1, minimum.last);
                  ps1.setInt(2,minimum.last);

                  rs = ps1.executeQuery();
              
              while(rs.next()){
                  Path newPath = new Path(minimum);
                  if(rs.getInt("IdCity1") == minimum.last && !visited[rs.getInt("IdCity2")-1]){
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity2"));
                      paths.add(newPath);
                      
                  }
                  if(rs.getInt("IdCity2") == minimum.last && !visited[rs.getInt("IdCity1")-1]){
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity1"));
                      paths.add(newPath);
                  }
                  
                  
              }
              
                  
            }
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
    public int shortestPathShopShop(int start,int end){
        Path startNode = new Path();
        startNode.setLast(start);
        Connection con = DB.getInstance().getConnection();
        
        visited = new boolean[numNodes];
        visited[start-1] = true;
        if(start == end) return 0;
        
         try(PreparedStatement ps1 = con.prepareStatement("select* from CityConnection where IdCity1=? or IdCity2=?")){
              
              ps1.setInt(1, start);
              ps1.setInt(2,start);
              ps1.execute();
              
              ResultSet rs = ps1.executeQuery();
              
              while(rs.next()){
                  Path newPath = new Path(startNode);
                  if(rs.getInt("IdCity1") == start){
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity2"));
                      paths.add(newPath);
                 
                  }else{
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity1"));
                      paths.add(newPath);
                   
                  }
                  
              }
              
              
              while(true){
                  int min = Integer.MAX_VALUE;
                  int index = -1;
                  Path minimum = new Path();
                  
                  for(int i=0;i<paths.size();i++){
                      Path p = paths.get(i);
                      if(p.price<min && visited[p.last-1]==false){
                          minimum = p;
                          min = p.price;
                          index = i;
                      }
                      
                  }
                  
                  
                  
                  paths.remove(index);
                  visited[minimum.last-1] = true;
                   if (end == minimum.last) {
                      return minimum.price;
                  }
              
                  PreparedStatement ps2 = con.prepareStatement("select* from CityConnection where IdCity1=? or IdCity2=?");
                  ps1.setInt(1, minimum.last);
                  ps1.setInt(2,minimum.last);

                  rs = ps1.executeQuery();
              
              while(rs.next()){
                  Path newPath = new Path(minimum);
                  if(rs.getInt("IdCity1") == minimum.last && !visited[rs.getInt("IdCity2")-1]){
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity2"));
                      paths.add(newPath);
                      
                  }
                  if(rs.getInt("IdCity2") == minimum.last && !visited[rs.getInt("IdCity1")-1]){
                      newPath.incrementPrice(rs.getInt("Distance"));
                      newPath.setLast(rs.getInt("IdCity1"));
                      paths.add(newPath);
                  }
                  
                  
              }
              
                  
            }
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
}
