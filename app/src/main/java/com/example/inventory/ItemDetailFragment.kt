package com.example.inventory


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.InventoryApplication
import com.example.inventory.data.Item
import com.example.inventory.data.getFormattedPrice
import com.example.inventory.databinding.FragmentItemDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [ItemDetailFragment] displays the details of the selected item.
 */
class ItemDetailFragment : Fragment() {

    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (requireActivity().application as InventoryApplication).dataBase.itemDao()
        )
    }

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    //ALAMACENAR LA INFO SOBRE UNA FILA DE DB
    lateinit var item: Item

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SE OBTIENE EL ID PASADO POR LOS ARGUMENTOS
        val id = navigationArgs.itemId

        //CON ESE ID, LO USAMOS COMO ARGUMENTO DEL MÉTODO QUE RECUPERA UN ITEM POR EL ID EN EL VIEWMODEL
        viewModel.retrieveItem(id).observe(
            viewLifecycleOwner
        ){ selectedItem ->
            item = selectedItem
            bind(item)
        }

    }

    private fun bind(item: Item){
        binding.apply {
            itemName.text = item.itemName
            itemPrice.text = item.getFormattedPrice()
            itemCount.text = item.quantityInStock.toString()

            //SI NO SE PUEDE VENDER EL ARTICULO SE DESHABILITA
            //EL BOTÓN DE LA AVENTA
            sellItem.isEnabled = viewModel.isStockAvailable(item)

            //EVENTO PARA VENDER / UPDATE UN ITEM
            sellItem.setOnClickListener {
                viewModel.sellItem(item)
            }

            //EVENTO AL ELIMINAR UN ITEM
            deleteItem.setOnClickListener {
                showConfirmationDialog()
            }

            //SE ENVIA A LA PANTALLA DE ADD, CON EL ID DEL ITEM PARA SER PRECARGADO
            //EN LA SIGUIENTE PÁGINA
            editItem.setOnClickListener {
                editItem()
            }
        }
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }

    /*MÉTODOS DE ACTUALIZAR*/
    private fun editItem(){
        val action = ItemDetailFragmentDirections
            .actionItemDetailFragmentToAddItemFragment(
                getString(R.string.edit_fragment_title),
                item.id
            )
        this.findNavController().navigate(action)
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
