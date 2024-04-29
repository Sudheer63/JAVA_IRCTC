
	$(document).ready(function()
			{
	var sno=1;
		var ele=`
            <tr>
            <td><input type="text" id="passengername" name="passengername" placeholder="Enter Name" required></td>
            <td>
                <select name="passengergender" id="gender" required>
                    <option value="" disabled selected>Gender </option>
                    <option value="male">M</option>
                    <option value="female">F</option>
                </select>
            </td>
            <td><input type="number" name="passengerage" id="age" placeholder="Age" required></td>
            <td>
                <select name="passengerberth" id="berth" required>
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
    	 	sno+=1;
    	 	$('tbody').append(ele);
console.log(sno);
    	 }
    		 });
$(document).on('click', 'table button', function(){
		if(sno>1){
        $(this).closest('tr').remove();
    	  	sno-=1;
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
			var train_name=$('#trains').val();
			var date=$('#date').val();
			var classtype=$('#classtype').val();
			var names=[];
			var age=[];
			var berth=[];
			var gender=[];
			  $('.passengers-table tbody tr').each(function() {
			        var name = $(this).find('#passengername').val();
			        var gen = $(this).find('#gender').val();
			         var ag = $(this).find('#age').val();
			          var br = $(this).find('#berth').val();
			        berth.push(br);
			        age.push(ag);
			        names.push(name);
			         gender.push(gen);
    			});
	    	$.ajax({
			    url: 'TrainBookingServlet?' +
    'source=' + encodeURIComponent(from) +
    '&destination=' + encodeURIComponent(to) +
    '&trains=' + encodeURIComponent(train_name) +
    '&traveldate=' + encodeURIComponent(date) +
    '&coach=' + encodeURIComponent(classtype) +
    '&nm=' + encodeURIComponent(names.join(',')) + // Convert array to comma-separated string
    '&gen=' + encodeURIComponent(gender.join(',')) + // Convert array to comma-separated string
    '&ag=' + encodeURIComponent(age.join(',')) + // Convert array to comma-separated string
    '&br=' + encodeURIComponent(berth.join(','))
,
			    type: 'POST', 
			    success: function(response) {
			        console.log('Data sent successfully:', response);
			    },
			    error: function(xhr, status, error) {
			        console.error('Error:', error);
			    }
	});
});
		

	
});