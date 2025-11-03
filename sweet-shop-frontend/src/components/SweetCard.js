import React, { useState } from 'react';
import {
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  Chip,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import { toast } from 'react-toastify';
import { sweetService } from '../services/authService';

const SweetCard = ({ sweet, onPurchaseSuccess }) => {
  const [openDialog, setOpenDialog] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(false);

  const handlePurchase = async () => {
    if (quantity < 1 || quantity > sweet.quantity) {
      toast.error('Invalid quantity');
      return;
    }

    setLoading(true);
    try {
      await sweetService.purchaseSweet(sweet.id, quantity);
      toast.success(`Successfully purchased ${quantity} ${sweet.name}(s)!`);
      setOpenDialog(false);
      setQuantity(1);
      if (onPurchaseSuccess) onPurchaseSuccess();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Purchase failed');
    } finally {
      setLoading(false);
    }
  };

  const isOutOfStock = sweet.quantity === 0;

  return (
    <>
      <Card
        sx={{
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          transition: 'transform 0.3s, box-shadow 0.3s',
          '&:hover': {
            transform: 'translateY(-8px)',
            boxShadow: 6,
          },
          borderRadius: 3,
          position: 'relative',
          opacity: isOutOfStock ? 0.7 : 1,
        }}
      >
        {isOutOfStock && (
          <Chip
            label="OUT OF STOCK"
            color="error"
            sx={{
              position: 'absolute',
              top: 16,
              right: 16,
              fontWeight: 'bold',
              zIndex: 1,
            }}
          />
        )}

        <Box
          sx={{
            height: 200,
            background: `linear-gradient(135deg, ${getGradientColors(sweet.category)})`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: 80,
          }}
        >
          {getCategoryEmoji(sweet.category)}
        </Box>

        <CardContent sx={{ flexGrow: 1 }}>
          <Typography gutterBottom variant="h5" component="div" fontWeight="bold">
            {sweet.name}
          </Typography>

          <Chip
            label={sweet.category}
            size="small"
            sx={{ mb: 2 }}
            color="primary"
            variant="outlined"
          />

          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {sweet.description || 'Delicious sweet treat!'}
          </Typography>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6" color="primary" fontWeight="bold">
              ${sweet.price}
            </Typography>
            <Chip
              label={`Stock: ${sweet.quantity}`}
              color={sweet.quantity > 10 ? 'success' : sweet.quantity > 0 ? 'warning' : 'error'}
              size="small"
            />
          </Box>
        </CardContent>

        <CardActions sx={{ p: 2, pt: 0 }}>
          <Button
            fullWidth
            variant="contained"
            startIcon={<ShoppingCartIcon />}
            disabled={isOutOfStock}
            onClick={() => setOpenDialog(true)}
            sx={{
              background: 'linear-gradient(45deg, #FF6B6B 30%, #FFE66D 90%)',
              fontWeight: 'bold',
            }}
          >
            {isOutOfStock ? 'Out of Stock' : 'Purchase'}
          </Button>
        </CardActions>
      </Card>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Purchase {sweet.name}</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <TextField
              autoFocus
              fullWidth
              label="Quantity"
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
              inputProps={{ min: 1, max: sweet.quantity }}
            />
            <Typography variant="body2" sx={{ mt: 2 }}>
              Available: {sweet.quantity}
            </Typography>
            <Typography variant="h6" sx={{ mt: 1 }}>
              Total: ${(sweet.price * quantity).toFixed(2)}
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button
            onClick={handlePurchase}
            variant="contained"
            disabled={loading}
          >
            Confirm Purchase
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

const getCategoryEmoji = (category) => {
  const emojis = {
    Cake: '🎂',
    Cookie: '🍪',
    Candy: '🍬',
    Chocolate: '🍫',
    'Ice Cream': '🍨',
    Pastry: '🥐',
  };
  return emojis[category] || '🍰';
};

const getGradientColors = (category) => {
  const gradients = {
    Cake: '#FF6B6B, #FFE66D',
    Cookie: '#FFA726, #FFE082',
    Candy: '#EC407A, #F48FB1',
    Chocolate: '#8D6E63, #D7CCC8',
    'Ice Cream': '#42A5F5, #90CAF9',
    Pastry: '#FFB74D, #FFE0B2',
  };
  return gradients[category] || '#9575CD, #CE93D8';
};

export default SweetCard;