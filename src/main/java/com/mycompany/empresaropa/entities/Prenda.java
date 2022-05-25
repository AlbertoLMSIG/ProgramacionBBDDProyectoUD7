/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.empresaropa.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author 34608
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "PRENDA")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Prenda.findAll", query = "SELECT p FROM Prenda p"),
    @javax.persistence.NamedQuery(name = "Prenda.findById", query = "SELECT p FROM Prenda p WHERE p.id = :id"),
    @javax.persistence.NamedQuery(name = "Prenda.findByTipoPrenda", query = "SELECT p FROM Prenda p WHERE p.tipoPrenda = :tipoPrenda"),
    @javax.persistence.NamedQuery(name = "Prenda.findByColor", query = "SELECT p FROM Prenda p WHERE p.color = :color"),
    @javax.persistence.NamedQuery(name = "Prenda.findByFechaCreacion", query = "SELECT p FROM Prenda p WHERE p.fechaCreacion = :fechaCreacion"),
    @javax.persistence.NamedQuery(name = "Prenda.findByPrecio", query = "SELECT p FROM Prenda p WHERE p.precio = :precio"),
    @javax.persistence.NamedQuery(name = "Prenda.findByNuevaColeccion", query = "SELECT p FROM Prenda p WHERE p.nuevaColeccion = :nuevaColeccion"),
    @javax.persistence.NamedQuery(name = "Prenda.findByFoto", query = "SELECT p FROM Prenda p WHERE p.foto = :foto")})
public class Prenda implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "ID")
    private Integer id;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "TIPO_PRENDA")
    private String tipoPrenda;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "COLOR")
    private String color;
    @javax.persistence.Column(name = "FECHA_CREACION")
    @javax.persistence.Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCreacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @javax.persistence.Column(name = "PRECIO")
    private BigDecimal precio;
    @javax.persistence.Column(name = "NUEVA_COLECCION")
    private Boolean nuevaColeccion;
    @javax.persistence.Column(name = "FOTO")
    private String foto;
    @javax.persistence.JoinColumn(name = "MARCA", referencedColumnName = "ID")
    @javax.persistence.ManyToOne
    private Marca marca;

    public Prenda() {
    }

    public Prenda(Integer id) {
        this.id = id;
    }

    public Prenda(Integer id, String tipoPrenda, String color) {
        this.id = id;
        this.tipoPrenda = tipoPrenda;
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(String tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getNuevaColeccion() {
        return nuevaColeccion;
    }

    public void setNuevaColeccion(Boolean nuevaColeccion) {
        this.nuevaColeccion = nuevaColeccion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prenda)) {
            return false;
        }
        Prenda other = (Prenda) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.empresaropa.entities.Prenda[ id=" + id + " ]";
    }
    
}
