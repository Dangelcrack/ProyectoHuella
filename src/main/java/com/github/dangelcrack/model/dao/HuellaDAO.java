package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object (DAO) para gestionar las entidades Huella utilizando Hibernate.
 * Proporciona métodos para realizar operaciones CRUD sobre las huellas en la base de datos.
 */
public class HuellaDAO {

    /**
     * Crea o inserta una nueva entidad Huella en la base de datos.
     *
     * @param huella la entidad Huella a crear.
     * @return true si la huella fue creada correctamente, false si hubo algún error.
     */
    public boolean crearHuella(Huella huella) {
        try (Session session = Connection.getInstance().getSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(huella);
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza una entidad Huella existente en la base de datos.
     *
     * @param huellaActualizada la entidad Huella actualizada.
     * @return true si la entidad fue actualizada correctamente, false si hubo algún error.
     */
    public boolean actualizarHuella(Huella huellaActualizada) {
        try (Session session = Connection.getInstance().getSession()) {
            Transaction transaction = session.beginTransaction();
            Huella huella = session.get(Huella.class, huellaActualizada.getId());
            if (huella != null) {
                session.merge(huellaActualizada);
                transaction.commit();
                return true;
            }
            transaction.commit();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una entidad Huella de la base de datos.
     *
     * @param huella la entidad Huella a eliminar.
     * @return true si la entidad fue eliminada correctamente, false si hubo algún error.
     */
    public boolean eliminarHuella(Huella huella) {
        try (Session session = Connection.getInstance().getSession()) {
            Transaction transaction = session.beginTransaction();
            Huella huellaExistente = session.get(Huella.class, huella.getId());
            if (huellaExistente != null) {
                session.remove(huellaExistente);
                transaction.commit();
                return true;
            }
            transaction.commit();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las huellas asociadas con un usuario específico.
     *
     * @param usuario la entidad Usuario que se utiliza para filtrar las huellas.
     * @return una lista de huellas asociadas con el usuario.
     */
    public List<Huella> obtenerHuellasPorUsuario(Usuario usuario) {
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery(
                    "SELECT h FROM Huella h " +
                            "JOIN FETCH h.idActividad " +
                            "JOIN FETCH h.idUsuario " +
                            "WHERE h.idUsuario = :usuario", Huella.class
            );
            query.setParameter("usuario", usuario);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene la cantidad de CO₂ emitida para una huella específica.
     *
     * @param huellaId el ID de la huella.
     * @return la cantidad de CO₂ emitida.
     */
    public double obtenerCantidadEmitida(Integer huellaId) {
        try (Session session = Connection.getInstance().getSession()) {
            Query<BigDecimal> query = session.createQuery(
                    "SELECT c.factorEmision * h.valor FROM Huella h " +
                            "JOIN Categoria c ON h.unidad = c.unidad " +
                            "WHERE h.id = :huellaId", BigDecimal.class
            );
            query.setParameter("huellaId", huellaId);
            BigDecimal result = query.uniqueResult();
            return result != null ? result.doubleValue() : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Obtiene el impacto total de CO₂ para una categoría específica.
     *
     * @param categoriaId el ID de la categoría.
     * @return el impacto total de CO₂.
     */
    public Double obtenerImpactoTotalPorCategoria(Integer categoriaId) {
        try (Session session = Connection.getInstance().getSession()) {
            Query<BigDecimal> query = session.createQuery(
                    "SELECT SUM(c.factorEmision * h.valor) FROM Huella h " +
                            "JOIN Categoria c ON h.unidad = c.unidad " +
                            "WHERE c.id = :categoriaId", BigDecimal.class
            );
            query.setParameter("categoriaId", categoriaId);
            BigDecimal result = query.uniqueResult();
            return result != null ? result.doubleValue() : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Obtiene la media de CO₂ emitido para una categoría específica en un número de días.
     *
     * @param categoriaId el ID de la categoría.
     * @param dias        el número de días para el cálculo.
     * @return la media de CO₂ emitido.
     */
    public Double obtenerMediaPorCategoria(Integer categoriaId, int dias) {
        try (Session session = Connection.getInstance().getSession()) {
            LocalDateTime fechaLimite = LocalDate.now().minusDays(dias).atStartOfDay();
            Query<Double> query = session.createQuery(
                    "SELECT CAST(AVG(c.factorEmision * h.valor) AS double) FROM Huella h " +
                            "JOIN Categoria c ON h.unidad = c.unidad " +
                            "WHERE c.id = :categoriaId AND h.fecha >= :fechaLimite", Double.class);
            query.setParameter("categoriaId", categoriaId);
            query.setParameter("fechaLimite", fechaLimite);  // Ahora pasando LocalDateTime
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0; // Retorna 0.0 si ocurre alguna excepción
        }
    }

    /**
     * Obtiene la media de CO₂ emitido para una categoría específica, considerando solo los días en los que hay huellas.
     *
     * @param categoriaId el ID de la categoría.
     * @return la media de CO₂ emitido por día, considerando solo los días con huellas.
     */
    public Double obtenerMediaDiariaPorCategoria(Integer categoriaId) {
        try (Session session = Connection.getInstance().getSession()) {
            // Consulta para obtener la suma de emisiones y la cantidad de huellas por día
            Query<Object[]> query = session.createQuery(
                    "SELECT h.fecha, SUM(c.factorEmision * h.valor), COUNT(h) " +
                            "FROM Huella h " +
                            "JOIN Categoria c ON h.unidad = c.unidad " +
                            "WHERE c.id = :categoriaId " +
                            "GROUP BY h.fecha", Object[].class);

            query.setParameter("categoriaId", categoriaId);

            // Ejecutamos la consulta
            List<Object[]> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return 0.0; // Si no hay resultados, retornamos 0.0
            }

            // Calcular el promedio de CO₂ por día, considerando solo días con huellas
            double totalPromedioDiario = 0.0;
            int cantidadDeDiasConHuellas = 0;

            for (Object[] result : resultList) {
                LocalDate fecha = ((LocalDateTime) result[0]).toLocalDate(); // Convertir a LocalDate
                Double sumaEmisiones = ((BigDecimal) result[1]).doubleValue();
                Long cantidadHuellas = (Long) result[2];

                // Si hay huellas para ese día, calculamos el promedio de ese día
                if (cantidadHuellas > 0) {
                    Double promedioDiario = sumaEmisiones / cantidadHuellas;
                    totalPromedioDiario += promedioDiario;
                    cantidadDeDiasConHuellas++;
                }
            }

            // Si no hay días con huellas, devolvemos 0.0
            if (cantidadDeDiasConHuellas == 0) {
                return 0.0;
            }

            // Promedio final de todos los días con huellas
            return totalPromedioDiario / cantidadDeDiasConHuellas;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0; // Si ocurre un error, retornamos 0.0
        }
    }
    /**
     * Método de fábrica para crear una instancia de HuellaDAO.
     *
     * @return una nueva instancia de HuellaDAO.
     */
    public static HuellaDAO build() {
        return new HuellaDAO();
    }
}