package interfaz;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import logica.Departamento;
import logica.Empleado;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;

import javax.swing.border.CompoundBorder;

/**
 * @author 1dam
 * @version 1, 2019
 * <h2> esta clase se encarga de realizar consultas estadísticas de 
 * empleados y departamentos. También obtiene un listado de empleados
 * y un listado de departamentos</h2>
 */
@SuppressWarnings("serial")
public class Consultas extends JDialog implements ActionListener  {

	ConsultasData Etiqueta = new ConsultasData(new JButton("Ver departamentos"), new JButton("Ver empleados"), new JButton("Estadisticas departamentos"), new JButton("Estadisticas empleados"));
	private ODB odb =null;
	private String bBDD;
	
	public Consultas() {
		setTitle("CONSULTAS A LA BD");
		setModal(true);
		setBounds(100, 100, 450, 340);
		Etiqueta.contentPane = new JPanel();
		setContentPane(Etiqueta.contentPane);
		Etiqueta.contentPane.setLayout(null);
		
		JLabel label_1 = new JLabel("CONSULTAS A LA BBDD");
		label_1.setForeground(Color.BLUE);
		label_1.setFont(new Font("Sylfaen", Font.BOLD, 15));
		label_1.setBounds(112, 24, 217, 32);
		Etiqueta.contentPane.add(label_1);
		
	
		Etiqueta.btnDepar.setBounds(111, 92, 218, 23);
		Etiqueta.contentPane.add(Etiqueta.btnDepar);
		

		Etiqueta.btnEmple.setBounds(111, 128, 218, 23);
		Etiqueta.contentPane.add(Etiqueta.btnEmple);
		
	
		Etiqueta.btnEstadDepar.setBounds(111, 164, 218, 23);
		Etiqueta.contentPane.add(Etiqueta.btnEstadDepar);
		
	
		Etiqueta.btnEstadEmple.setBounds(111, 200, 218, 23);
		Etiqueta.contentPane.add(Etiqueta.btnEstadEmple);
		
		JPanel panel = new JPanel();
		panel.setBorder(new CompoundBorder());
		panel.setBackground(Color.GREEN);
		panel.setBounds(60, 67, 314, 181);
		Etiqueta.contentPane.add(panel);
		
		Etiqueta.lblResultado = new JLabel("---------------------------------------------------------------------");
		Etiqueta.lblResultado.setForeground(Color.RED);
		Etiqueta.lblResultado.setFont(new Font("Dialog", Font.BOLD, 14));
		Etiqueta.lblResultado.setBounds(44, 272, 345, 14);
		Etiqueta.contentPane.add(Etiqueta.lblResultado);
		
		
		Etiqueta.btnDepar.addActionListener(this);
		Etiqueta.btnEmple.addActionListener(this);
		Etiqueta.btnEstadDepar.addActionListener(this);
		Etiqueta.btnEstadEmple.addActionListener(this);
	
	}	

public void actionPerformed(ActionEvent e) 
{   
	
	bBDD = "Empleados.dat";
	odb = ODBFactory.open(bBDD);
	
    if (e.getSource() == Etiqueta.btnDepar) { consuldepart(0, 0);  	}
	
	if (e.getSource() == Etiqueta.btnEmple) { consulemple();  	}
	
	if (e.getSource() == Etiqueta.btnEstadDepar) { estadisdepart();  	}
	
	if (e.getSource() == Etiqueta.btnEstadEmple) { estadisemple();  	}
	
}		
	
/////////////////////////////////////////////////////////////////

public String consuldepart(int param1, int param2) {
				IQuery query=new CriteriaQuery(Departamento.class);
				query.orderByAsc("dept_no");
				Objects<Departamento> depar=odb.getObjects(query);
				
				if(!depar.isEmpty()){
					int cont=depar.size();
					String cabecera=String.format("%10s  %15s  %15s  %15s  %1s",
							"Num depart", "Nombre", "Población", "Num Empleados", "Salario Medio");
					System.out.println(cabecera);
					System.out.println("-----------------------------------------------------------------------------");
					for(Departamento d:depar){					
						Values values=odb.getValues(new ValuesCriteriaQuery(Empleado.class,
								Where.equal("dept.dept_no", d.getDept_no()))
								.count("emp_no")
								.sum("salario"));
						
						if(!values.isEmpty()){
							ObjectValues ob=values.nextValues();
							double media=((BigDecimal)ob.getByIndex(1)).doubleValue()/((BigInteger)ob.getByIndex(0)).intValue();
							BigInteger count=(BigInteger)ob.getByIndex(0);
							
							String salida=String.format("%10s  %15s  %15s  %15s  %.2f", 
									d.getDept_no(), d.getDnombre(), d.getLoc(), count.toString(),
									(Double.isNaN(media) ? 0 : media));
							
							System.out.println(salida);
						}
						Etiqueta.lblResultado.setText("Hay "+cont+" departamentos");
					}
				}
				else{
					String error = "No existen datos de departamentos";
					System.out.println(error);
					Etiqueta.lblResultado.setText(error);
				}
				System.out.println("-----------------------------------------------------------------------------");
				odb.close();
				return bBDD;
			}
///////////////////////////////////////////////////////////////
public void consulemple() {		
		//Consultar empleados
	
			IQuery query=new CriteriaQuery(Empleado.class);
				query.orderByAsc("emp_no");
				Objects<Empleado> emp=odb.getObjects(query);
				
				if(!emp.isEmpty()){
					int cont=emp.size();
					String cabecera=String.format("%11s  %15s  %15s  %15s  %10s  %15s  %10s",
							"NumEmpleado", "Nombre", "Dirección", "Oficio", "Salario","NumDep", "NombreDep");
					System.out.println(cabecera);
					System.out.println("-------------------------------------------------------------------------------------------------------");
					for(Empleado e:emp){
						String salida=String.format("%11s  %15s  %15s  %15s  %10s  ",
								e.getEmp_no(), e.getNombre(), e.getPobla(),
								e.getOficio(), e.getSalario());
						if(e.getDept()!=null)
							salida+=String.format("%15s  %10s", e.getDept().getDept_no(), e.getDept().getDnombre());
						else
							salida+=String.format("%30s", "No tiene departamento asociado");
						System.out.println(salida);
					}
					Etiqueta.lblResultado.setText("Hay "+cont+" empleados");
				}
				else{
					String error = "No existen datos de empleados";
					System.out.println(error);
					Etiqueta.lblResultado.setText(error);
				}
				System.out.println("-------------------------------------------------------------------------------------------------------\n");
				odb.close();
			}
//////////////////////////////////
//Estadisticas departamentos
public void estadisdepart() {

				int max=0;
				double maxSal=0;
				String nombre=null;
				String nombreSal=null;
				
				Values values=odb.getValues(new ValuesCriteriaQuery(Empleado.class, Where.isNotNull("dept")).
						field("dept.dnombre").count("emp_no").sum("salario").groupBy("dept.dnombre"));
							
				for(ObjectValues o:values){
					int intValue=((BigInteger)o.getByIndex(1)).intValue();
					if(intValue>max){
						max=intValue;
						nombre=(String)o.getByIndex(0);
					}
					if (((BigInteger)o.getByIndex(1)).intValue() !=0)
					{
					  double mediaSal=((BigDecimal)o.getByIndex(2)).doubleValue() / ((BigInteger)o.getByIndex(1)).intValue();
					  if(mediaSal>maxSal){
						maxSal=mediaSal;
						nombreSal=(String)o.getByIndex(0);
					}
					}
				}
				if(nombre!=null) {
					System.out.println("El departamento con mas empleados es: "+nombre+" con "+max+" empleados");
					System.out.println("El departamento con mas media de salario es: "+nombreSal+" con "+maxSal+"€");
					Etiqueta.lblResultado.setText("Estadisticas de departamentos mostradas");
				}
				else{
					String error = "No hay estadisticas de departamentos";
					System.out.println(error);
					Etiqueta.lblResultado.setText(error);
				}
				
				System.out.println("-----------------------------------------------\n");
				odb.close();
			}
/////////////////////////////////////////////////////////
		
//Estadisticas empleados
	public void estadisemple() {
					
				//Empleado con mas salario
				Values values=odb.getValues(new ValuesCriteriaQuery(Empleado.class, 
						Where.equal("salario", odb.getValues(new ValuesCriteriaQuery(Empleado.class).max("salario")).next().getByIndex(0)))
						.field("salario").field("nombre", "nom"));
				if(!values.isEmpty()){
					ObjectValues o=values.next();
					System.out.println("El empleado con mas salario es: "+o.getByAlias("nom")+" con un salario de "+o.getByIndex(0)+"€");
					
					//Media de salario
					values=odb.getValues(new ValuesCriteriaQuery(Empleado.class).count("emp_no").sum("salario"));
					ObjectValues o2=values.next();
					double media=((BigDecimal)o2.getByIndex(1)).doubleValue()/((BigInteger)o2.getByIndex(0)).intValue();
					System.out.println("La media de salario de los empleados es: "+media+"€");
					
					//Numero de empleados por oficio
					values=odb.getValues(new ValuesCriteriaQuery(Empleado.class).count("emp_no").field("oficio").groupBy("oficio"));
					String cabecera=String.format("%15s  %10s", "Oficio", "Num emple");
					System.out.println(cabecera);
					System.out.println("---------------------------");
					for(ObjectValues o3:values){
						String datos=String.format("%15s  %10s", o3.getByIndex(1), o3.getByIndex(0));
						System.out.println(datos);
					}
					Etiqueta.lblResultado.setText("Estadisticas de empleados mostradas");
				}
				else{
					String error = "No hay empleados";
					System.out.println(error);
					Etiqueta.lblResultado.setText(error);
				}
				System.out.println("---------------------------\n");
				odb.close();
			}
////////////////////////////////////////
}
