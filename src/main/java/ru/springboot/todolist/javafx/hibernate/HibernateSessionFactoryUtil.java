//package ru.javabegin.training.javafx.hibernate;
//
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.service.ServiceRegistry;
//
//
//public class HibernateSessionFactoryUtil {
//
//    private static SessionFactory sessionFactory;
//
//    private static ServiceRegistry serviceRegistry;
//
//    public HibernateSessionFactoryUtil() {
//    }
//
//    /**
//     * В этом классе я создаю конфигурацию и добавляю к ней класс с конфигурацией JPA
//     */
//    public static SessionFactory getSessionFactory(){
//        if(sessionFactory == null){
//            try{
//                Configuration configuration = new Configuration().configure();
//                configuration.addAnnotatedClass(ru.javabegin.training.javafx.entity.Task.class);
//                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
//                sessionFactory = configuration.buildSessionFactory(builder.build());
//            } catch (Exception e) {
//                System.out.println("Exception !" + e);
//            }
//        }
//        return sessionFactory;
//    }
//
//
//}
