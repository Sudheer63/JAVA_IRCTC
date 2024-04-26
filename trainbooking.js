
	$(document).ready(function()
			{
	var sno=1;
		var ele=`
            <tr>
            <td >${sno}</td>
            <td><input type="text" name="passengername" placeholder="Enter Name" required></td>
            <td>
                <select name="passengergender" required>
                    <option value="" disabled selected>Gender </option>
                    <option value="male">M</option>
                    <option value="female">F</option>
                </select>
            </td>
            <td><input type="number" name="passengerage" placeholder="Age" required></td>
            <td>
                <select name="passengerberth" required>
                    <option value="" disabled selected>Select</option>
                    <option value="lower">Lower</option>
                    <option value="middle">Middle</option>
                    <option value="upper">Upper</option>
                </select>
            </td>
            <td><button class="delete-btn">Delete</button></td>
        </tr>`
        $('tbody').append(ele);
     $('.add-passenger-btn').click(function()
    		 {
		if(sno<5)
		{
    	 	sno++;
    	 	$('tbody').append(ele);
console.log(sno);
    	 }
    		 });
$(document).on('click', 'table button', function(){
		if(sno>1){
        $(this).closest('tr').remove();
    	  	sno--;
console.log(sno);
		}
    });
    $.ajax({
        url: 'AllStationServlet',
        type: 'GET',
        success: function(data)
        {
        	 var source = $('#source');
			 var destination =$('#destination');
        	 var stationNames = data.split(',');
             $.each(stationNames, function(index, stationName) {
                 source.append($('<option></option>').text(stationName).val(stationName));
			 	 destination.append($('<option></option>').text(stationName).val(stationName));
             });
         },
         error: function(xhr, status, error) {
             console.error("Error fetching data:", error);
             alert('Error fetching data');
         }
     });
	$('#source, #destination').change(function()
	{
		var vad1=$('#source').val();
		var vad2=$('#destination').val();
		console.log(vad1+vad2);
		  if ((vad1 != null && vad2 != null) && (vad1 == vad2)) 
			{
                    alert('Error: From and To stations must be different');
                    $(this).val(''); 
            }
		 else if((vad1 != null && vad2 != null) && (vad1 != vad2)) 
		{
		
	    $.ajax({
		        url: 'TrainServlet',
		        type: 'Post',
                data: { 'source': vad1, 'destination': vad2 },
		        success: function(response)
		        {
		        	 console.log("successfully sent source and destination")
					 var trainname=response.split(',');
					$('#trains option').remove();
					$.each(trainname,function(i,name)
					{
						$('#trains').append($('<option></option>').text(name))
					});	
					 
		         },
		         error: function(xhr, status, error) {
		             console.error("Error fetching data:", error);
		             alert('Error fetching data');
		         }
		     });	
		}
	})
	$('.fifth button').click(function(){
		var from=$('#source').val();
		var to=$('#destination').val();
		var trainname=$('#trains').val();
		var date=$('#date').val();
		var coach=$('#classtype option:selected').val();
		var passengerData = [];
        $('.passengers-table tbody tr').each(function() {
            var name =$(this).find("td:").val();

			console.log(name);
        });
		console.log(from+" "+to+" "+trainname+" "+date+" "+coach);
	
	});
});
